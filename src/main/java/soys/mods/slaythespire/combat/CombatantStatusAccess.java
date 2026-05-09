package soys.mods.slaythespire.combat;

import net.minecraft.world.entity.LivingEntity;

/**
 * 中文：LivingEntity CombatantStatus 的强制访问工具。所有状态牌/伤害修正都通过它取状态。
 * English: Strict accessor for LivingEntity CombatantStatus. Status cards and damage modifiers read statuses through this helper.
 */
public final class CombatantStatusAccess {
    private CombatantStatusAccess() {
    }

    public static CombatantStatus get(LivingEntity entity) {
        return entity.getCapability(CombatantStatusProvider.CAPABILITY)
                .orElseThrow(() -> new IllegalStateException("Missing combatant status for " + entity.getScoreboardName()));
    }
}
