package soys.mods.slaythespire.card;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import soys.mods.slaythespire.combat.CombatState;

public record CardUseContext(ServerPlayer player, CombatState state, CardDefinition definition, ItemStack stack, @Nullable LivingEntity target) {
}
