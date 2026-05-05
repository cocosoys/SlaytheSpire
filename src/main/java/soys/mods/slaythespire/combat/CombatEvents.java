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

public final class CombatEvents {
    private static final ResourceLocation COMBAT_STATE_ID = ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "combat_state");
    private static final ResourceLocation COMBATANT_EFFECTS_ID = ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "combatant_status");

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
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
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide || !(event.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        CombatService.tick(serverPlayer);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim instanceof ServerPlayer serverPlayer) {
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
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        resetCombatState(event);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        resetCombatState(event);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        resetCombatState(event);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        resetCombatState(event);
    }

    private static void resetCombatState(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CombatService.safeExitCombat(serverPlayer, null);
        }
    }
}
