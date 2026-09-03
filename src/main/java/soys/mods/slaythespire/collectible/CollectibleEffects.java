package soys.mods.slaythespire.collectible;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.combat.CombatService;
import soys.mods.slaythespire.combat.CombatState;

import java.util.HashMap;
import java.util.Map;

/**
 * 中文：收藏品效果注册表。遗物效果按钩子类型分组，在战斗不同阶段触发；药水效果通过使用触发。
 * English: Registry for collectible effects. Relic effects are grouped by hook type and trigger at different combat phases; potion effects trigger on use.
 */
public final class CollectibleEffects {

    // 中文：遗物效果触发钩子类型。
    // English: Hook types for relic effect triggering.
    public enum Hook {
        COMBAT_START,
        TURN_END,
        COMBAT_END
    }

    private record RelicEntry(CollectibleEffect effect, Hook hook) {
    }

    private static final Map<ResourceLocation, RelicEntry> RELIC_EFFECTS = new HashMap<>();
    private static final Map<ResourceLocation, CollectibleEffect> POTION_EFFECTS = new HashMap<>();

    static {
        // ==================== 遗物效果 ====================
        // 中文：燃烧之血 — 战斗结束后恢复 6 点生命。
        registerRelic("relic_burningblood", Hook.COMBAT_END, ctx -> ctx.player().heal(6.0F));
        // 中文：金刚 — 战斗开始时获得 1 点力量。
        registerRelic("relic_vajra", Hook.COMBAT_START, ctx -> ctx.state().addStrength(1));
        // 中文：山铜 — 回合结束时如果没有格挡，获得 6 点格挡。
        registerRelic("relic_orichalcum", Hook.TURN_END, ctx -> {
            if (ctx.state().getBlock() <= 0) {
                CombatService.gainBlock(ctx.player(), 6, false);
            }
        });
        // 中文：肉 — 战斗结束时如果生命低于 50%，恢复 6 点生命。
        registerRelic("relic_meat", Hook.COMBAT_END, ctx -> {
            if (ctx.player().getHealth() < ctx.player().getMaxHealth() * 0.5F) {
                ctx.player().heal(6.0F);
            }
        });
        // 中文：红头骨 — 战斗开始时生命低于 50% 则获得 3 点力量。
        registerRelic("relic_red_skull", Hook.COMBAT_START, ctx -> {
            if (ctx.player().getHealth() < ctx.player().getMaxHealth() * 0.5F) {
                ctx.state().addStrength(3);
            }
        });
        // 中文：纸鹤 — 战斗开始时获得 1 层易伤但获得 1 点能量。
        registerRelic("relic_papercrane", Hook.COMBAT_START, ctx -> {
            CombatService.applyVulnerable(ctx.player(), 1);
            CombatService.gainEnergy(ctx.player(), 1);
        });
        // 中文：蓝蜡烛 — 战斗开始时获得 1 点能量。
        registerRelic("relic_bluecandle", Hook.COMBAT_START, ctx -> CombatService.gainEnergy(ctx.player(), 1));
        // 中文：折扇 — 战斗开始时抽 1 张牌。
        registerRelic("relic_ornamentalfan", Hook.COMBAT_START, ctx -> CombatService.drawTemporaryCards(ctx.player(), 1));

        // ==================== 药水效果 ====================
        // 中文：生命药水 — 恢复 8 点生命。
        registerPotion("potion_heart_body", ctx -> ctx.player().heal(8.0F));
        // 中文：力量药水 — 获得 2 点力量。
        registerPotion("potion_anvil_body", ctx -> ctx.state().addStrength(2));
        // 中文：格挡药水 — 获得 12 点格挡。
        registerPotion("potion_bottle_body", ctx -> CombatService.gainBlock(ctx.player(), 12));
        // 中文：能量药水 — 获得 2 点能量。
        registerPotion("potion_bolt_body", ctx -> CombatService.gainEnergy(ctx.player(), 2));
        // 中文：敏捷药水 — 抽 2 张牌。
        registerPotion("potion_fairy_body", ctx -> CombatService.drawTemporaryCards(ctx.player(), 2));
        // 中文：虚弱药水 — 对目标施加 2 层虚弱。
        registerPotion("potion_ghost_body", ctx -> {
            if (ctx.target() != null) {
                CombatService.applyWeak(ctx.target(), 2);
            }
        });
        // 中文：易伤药水 — 对目标施加 2 层易伤。
        registerPotion("potion_eye_body", ctx -> {
            if (ctx.target() != null) {
                CombatService.applyVulnerable(ctx.target(), 2);
            }
        });
        // 中文：火焰药水 — 对所有敌人造成 8 点伤害。
        registerPotion("potion_sphere_body", ctx -> CombatService.dealAllEnemies(ctx.player(), 8.0F));
    }

    private CollectibleEffects() {
    }

    // 中文：注册遗物效果。
    // English: Registers a relic effect.
    private static void registerRelic(String id, Hook hook, CollectibleEffect effect) {
        RELIC_EFFECTS.put(ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, id), new RelicEntry(effect, hook));
    }

    // 中文：注册药水效果。
    // English: Registers a potion effect.
    private static void registerPotion(String id, CollectibleEffect effect) {
        POTION_EFFECTS.put(ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, id), effect);
    }

    // 中文：获取遗物效果，不存在时返回 null。
    // English: Gets a relic effect, returning null when absent.
    public static CollectibleEffect getRelicEffect(ResourceLocation id) {
        RelicEntry entry = RELIC_EFFECTS.get(id);
        return entry == null ? null : entry.effect();
    }

    // 中文：获取药水效果，不存在时返回 null。
    // English: Gets a potion effect, returning null when absent.
    public static CollectibleEffect getPotionEffect(ResourceLocation id) {
        return POTION_EFFECTS.get(id);
    }

    // 中文：检查遗物是否有注册效果。
    // English: Checks whether a relic has a registered effect.
    public static boolean hasRelicEffect(ResourceLocation id) {
        return RELIC_EFFECTS.containsKey(id);
    }

    // 中文：检查药水是否有注册效果。
    // English: Checks whether a potion has a registered effect.
    public static boolean hasPotionEffect(ResourceLocation id) {
        return POTION_EFFECTS.containsKey(id);
    }

    // 中文：触发所有已携带遗物的指定钩子效果。
    // English: Triggers the specified hook effect for all carried relics.
    public static void triggerRelicHook(ServerPlayer player, CombatState state, LivingEntity target, ItemStack stack, Hook hook) {
        CollectibleEffect.Context context = new CollectibleEffect.Context(player, state, target, stack);
        for (ItemStack invStack : player.getInventory().items) {
            if (invStack.getItem() instanceof CollectibleItem item) {
                CollectibleDefinition def = item.definition();
                if (def.kind() == CollectibleKind.RELIC) {
                    RelicEntry entry = RELIC_EFFECTS.get(def.id());
                    if (entry != null && entry.hook() == hook) {
                        entry.effect().apply(context);
                    }
                }
            }
        }
    }
}
