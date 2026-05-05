package soys.mods.slaythespire.combat;

import net.minecraft.world.entity.LivingEntity;

public final class CombatantStatusAccess {
    private CombatantStatusAccess() {
    }

    public static CombatantStatus get(LivingEntity entity) {
        return entity.getCapability(CombatantStatusProvider.CAPABILITY)
                .orElseThrow(() -> new IllegalStateException("Missing combatant status for " + entity.getScoreboardName()));
    }
}
