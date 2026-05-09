package soys.mods.slaythespire.combat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.network.ModNetworking;

/**
 * 中文：Forge 事件桥接层。负责挂载 Capability、驱动服务端战斗 tick、处理受伤修正，并在玩家生命周期变化时清理战斗。
 * English: Forge event bridge. It attaches Capabilities, drives server combat ticks, adjusts hurt events, and clears combat on player lifecycle changes.
 */
public final class CombatEvents {
    private static final ResourceLocation COMBAT_STATE_ID = ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "combat_state");
    private static final ResourceLocation COMBATANT_EFFECTS_ID = ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "combatant_status");

    @SubscribeEvent
    // 中文：为玩家和生物挂载本模组的战斗 Capability。
    // English: Attaches this mod's combat Capabilities to players and living entities.
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        // 中文：玩家需要完整 CombatState；所有 LivingEntity 需要 CombatantStatus，方便敌人也能吃易伤/虚弱。
        // English: Players need full CombatState; every LivingEntity needs CombatantStatus so enemies can also receive vulnerable and weak.
        if (event.getObject() instanceof Player) {
            CombatStateProvider provider = new CombatStateProvider();
            event.addCapability(COMBAT_STATE_ID, provider);
            event.addListener(provider::invalidate);
        }

        if (event.getObject() instanceof LivingEntity) {
            CombatantStatusProvider provider = new CombatantStatusProvider();
            event.addCapability(COMBATANT_EFFECTS_ID, provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    // 中文：在服务端玩家 tick 末尾推进战斗超时和预览同步。
    // English: Advances combat timeout and preview synchronization at the end of server player ticks.
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide || !(event.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        CombatService.tick(serverPlayer);
    }

    @SubscribeEvent
    // 中文：拦截生物受伤事件并应用卡牌战斗的易伤、虚弱、力量和格挡规则。
    // English: Intercepts living hurt events and applies card-combat vulnerable, weak, strength, and block rules.
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim instanceof ServerPlayer serverPlayer) {
            // 中文：玩家受伤时按杀戮尖塔规则先修正易伤/虚弱/力量，再用格挡吸收剩余伤害。
            // English: When a player is hurt, Slay the Spire modifiers are applied first, then block absorbs the remaining damage.
            CombatState state = CombatStateAccess.get(serverPlayer);
            CombatantStatus victimStatus = CombatantStatusAccess.get(serverPlayer);

            if (victimStatus.vulnerable() > 0) {
                event.setAmount(event.getAmount() * 1.5F);
            }

            Entity sourceEntity = event.getSource().getEntity();
            if (sourceEntity instanceof LivingEntity attacker) {
                CombatantStatus attackerStatus = CombatantStatusAccess.get(attacker);
                float adjusted = event.getAmount() + attackerStatus.strength();
                if (attackerStatus.weak() > 0) {
                    adjusted *= 0.75F;
                }
                event.setAmount(Math.max(0.0F, adjusted));

                if (state.isInCombat() && state.getFlameBarrierDamage() > 0) {
                    attacker.hurt(serverPlayer.damageSources().thorns(serverPlayer), state.getFlameBarrierDamage());
                }
            }

            if (state.isInCombat() && state.getBlock() > 0 && event.getAmount() > 0.0F) {
                // 中文：格挡变化必须立即同步，否则客户端 HUD 会显示旧格挡。
                // English: Block changes sync immediately so the client HUD does not show stale block.
                int absorbed = state.absorbDamage(event.getAmount());
                if (absorbed > 0) {
                    event.setAmount(Math.max(0.0F, event.getAmount() - absorbed));
                    ModNetworking.sync(serverPlayer);
                }
            }
            return;
        }

    }

    @SubscribeEvent
    // 中文：玩家登录时清理残留战斗状态。
    // English: Clears leftover combat state when a player logs in.
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        resetCombatState(event);
    }

    @SubscribeEvent
    // 中文：玩家重生时清理残留战斗状态。
    // English: Clears leftover combat state when a player respawns.
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        resetCombatState(event);
    }

    @SubscribeEvent
    // 中文：玩家切换维度时清理残留战斗状态。
    // English: Clears leftover combat state when a player changes dimension.
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        resetCombatState(event);
    }

    @SubscribeEvent
    // 中文：玩家退出时清理残留战斗状态。
    // English: Clears leftover combat state when a player logs out.
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        resetCombatState(event);
    }

    // 中文：统一执行玩家生命周期变化后的战斗状态清理。
    // English: Runs the shared combat-state cleanup after player lifecycle changes.
    private static void resetCombatState(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 中文：登录、重生、切维度和退出都走同一清理路径，避免跨世界残留战斗状态。
            // English: Login, respawn, dimension change, and logout share one cleanup path to prevent cross-world combat-state leftovers.
            CombatService.safeExitCombat(serverPlayer, null);
        }
    }
}
