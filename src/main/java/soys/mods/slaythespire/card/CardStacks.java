package soys.mods.slaythespire.card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class CardStacks {
    private static final String GENERATED = "sts_generated";
    private static final String EXPIRES_END_TURN = "sts_expires_end_turn";
    private static final String USED_THIS_TURN = "sts_used_this_turn";
    private static final String USED_THIS_COMBAT = "sts_used_this_combat";
    private static final String COST_OVERRIDE = "sts_cost_override";
    private static final String PREVIEW_STRENGTH = "sts_preview_strength";
    private static final String PREVIEW_BLOCK = "sts_preview_block";
    private static final String PREVIEW_ENERGY = "sts_preview_energy";
    private static final String PREVIEW_STRIKE_COUNT = "sts_preview_strike_count";
    private static final String PREVIEW_HP_LOSS = "sts_preview_hp_loss";
    private static final String PREVIEW_CORRUPTION = "sts_preview_corruption";

    private CardStacks() {
    }

    public static void setGenerated(ItemStack stack, boolean value) {
        tag(stack).putBoolean(GENERATED, value);
    }

    public static boolean isGenerated(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(GENERATED);
    }

    public static void setExpiresEndTurn(ItemStack stack, boolean value) {
        tag(stack).putBoolean(EXPIRES_END_TURN, value);
    }

    public static boolean expiresEndTurn(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(EXPIRES_END_TURN);
    }

    public static void setUsedThisTurn(ItemStack stack, boolean value) {
        tag(stack).putBoolean(USED_THIS_TURN, value);
    }

    public static boolean isUsedThisTurn(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(USED_THIS_TURN);
    }

    public static void setUsedThisCombat(ItemStack stack, boolean value) {
        tag(stack).putBoolean(USED_THIS_COMBAT, value);
    }

    public static boolean isUsedThisCombat(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(USED_THIS_COMBAT);
    }

    public static void setCostOverride(ItemStack stack, Integer value) {
        CompoundTag tag = tag(stack);
        if (value == null) {
            tag.remove(COST_OVERRIDE);
            return;
        }

        tag.putInt(COST_OVERRIDE, value);
    }

    public static Integer getCostOverride(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(COST_OVERRIDE) ? stack.getTag().getInt(COST_OVERRIDE) : null;
    }

    public static void setPreviewStrength(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_STRENGTH, value);
    }

    public static int getPreviewStrength(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_STRENGTH) : 0;
    }

    public static void setPreviewBlock(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_BLOCK, value);
    }

    public static int getPreviewBlock(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_BLOCK) : 0;
    }

    public static void setPreviewEnergy(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_ENERGY, value);
    }

    public static int getPreviewEnergy(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_ENERGY) : 0;
    }

    public static void setPreviewStrikeCount(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_STRIKE_COUNT, value);
    }

    public static int getPreviewStrikeCount(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_STRIKE_COUNT) : 0;
    }

    public static void setPreviewHpLoss(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_HP_LOSS, value);
    }

    public static int getPreviewHpLoss(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_HP_LOSS) : 0;
    }

    public static void setPreviewCorruption(ItemStack stack, boolean value) {
        tag(stack).putBoolean(PREVIEW_CORRUPTION, value);
    }

    public static boolean isPreviewCorruption(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(PREVIEW_CORRUPTION);
    }

    public static void clearTurnFlags(ItemStack stack) {
        if (!stack.hasTag()) {
            return;
        }

        stack.getTag().remove(USED_THIS_TURN);
        stack.getTag().remove(COST_OVERRIDE);
        if (stack.getTag().isEmpty()) {
            stack.setTag(null);
        }
    }

    public static void clearCombatFlags(ItemStack stack) {
        if (!stack.hasTag()) {
            return;
        }

        stack.getTag().remove(USED_THIS_TURN);
        stack.getTag().remove(USED_THIS_COMBAT);
        stack.getTag().remove(COST_OVERRIDE);
        stack.getTag().remove(EXPIRES_END_TURN);
        if (!isGenerated(stack)) {
            stack.getTag().remove(GENERATED);
        }
        if (stack.getTag().isEmpty()) {
            stack.setTag(null);
        }
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrCreateTag();
    }
}
