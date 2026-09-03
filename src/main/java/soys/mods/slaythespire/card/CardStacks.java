package soys.mods.slaythespire.card;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * 中文：卡牌 ItemStack 的 NBT 辅助类。战斗中临时生成、已使用、费用覆盖和 tooltip 预览值都存在这里。
 * English: NBT helper for card ItemStacks. Generated flags, used-this-combat state, cost overrides, and tooltip preview values are stored here.
 */
public final class CardStacks {
    // 中文：这些 key 是物品栈私有状态，不写入 CardDefinition，避免定义和单张卡的临时状态混淆。
    // English: These keys are per-stack private state and stay out of CardDefinition, avoiding a mix of definition data and temporary card state.
    private static final String GENERATED = "sts_generated";
    private static final String USED_THIS_COMBAT = "sts_used_this_combat";
    private static final String COST_OVERRIDE = "sts_cost_override";
    private static final String PREVIEW_STRENGTH = "sts_preview_strength";
    private static final String PREVIEW_BLOCK = "sts_preview_block";
    private static final String PREVIEW_ENERGY = "sts_preview_energy";
    private static final String PREVIEW_STRIKE_COUNT = "sts_preview_strike_count";
    private static final String PREVIEW_HP_LOSS = "sts_preview_hp_loss";
    private static final String PREVIEW_CORRUPTION = "sts_preview_corruption";
    private static final String IN_HAND = "sts_in_hand";
    private static final String EXHAUSTED = "sts_exhausted";

    // 中文：禁止实例化纯静态 NBT 工具类。
    // English: Prevents instantiation of this static-only NBT utility.
    private CardStacks() {
    }

    // 中文：标记物品栈是否为战斗中临时生成的卡牌。
    // English: Marks whether the stack is a card temporarily generated during combat.
    public static void setGenerated(ItemStack stack, boolean value) {
        tag(stack).putBoolean(GENERATED, value);
    }

    // 中文：读取物品栈是否为战斗中临时生成的卡牌。
    // English: Reads whether the stack is a card temporarily generated during combat.
    public static boolean isGenerated(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(GENERATED);
    }

    // 中文：标记物品栈在本场战斗中是否已经使用过。
    // English: Marks whether the stack has already been used in this combat.
    public static void setUsedThisCombat(ItemStack stack, boolean value) {
        tag(stack).putBoolean(USED_THIS_COMBAT, value);
    }

    // 中文：读取物品栈在本场战斗中是否已经使用过。
    // English: Reads whether the stack has already been used in this combat.
    public static boolean isUsedThisCombat(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(USED_THIS_COMBAT);
    }

    // 中文：设置或清除单张卡牌的费用覆盖值。
    // English: Sets or clears the per-card cost override.
    public static void setCostOverride(ItemStack stack, Integer value) {
        CompoundTag tag = tag(stack);
        if (value == null) {
            tag.remove(COST_OVERRIDE);
            return;
        }

        tag.putInt(COST_OVERRIDE, value);
    }

    // 中文：读取单张卡牌的费用覆盖值，未设置时返回 null。
    // English: Reads the per-card cost override, returning null when unset.
    public static Integer getCostOverride(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(COST_OVERRIDE) ? stack.getTag().getInt(COST_OVERRIDE) : null;
    }

    // 中文：写入 tooltip 预览用的力量数值。
    // English: Stores the strength value used by tooltip preview text.
    public static void setPreviewStrength(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_STRENGTH, value);
    }

    // 中文：读取 tooltip 预览用的力量数值。
    // English: Reads the strength value used by tooltip preview text.
    public static int getPreviewStrength(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_STRENGTH) : 0;
    }

    // 中文：写入 tooltip 预览用的格挡数值。
    // English: Stores the block value used by tooltip preview text.
    public static void setPreviewBlock(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_BLOCK, value);
    }

    // 中文：读取 tooltip 预览用的格挡数值。
    // English: Reads the block value used by tooltip preview text.
    public static int getPreviewBlock(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_BLOCK) : 0;
    }

    // 中文：写入 tooltip 预览用的能量数值。
    // English: Stores the energy value used by tooltip preview text.
    public static void setPreviewEnergy(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_ENERGY, value);
    }

    // 中文：读取 tooltip 预览用的能量数值。
    // English: Reads the energy value used by tooltip preview text.
    public static int getPreviewEnergy(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_ENERGY) : 0;
    }

    // 中文：写入 tooltip 预览用的打击牌数量。
    // English: Stores the Strike-card count used by tooltip preview text.
    public static void setPreviewStrikeCount(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_STRIKE_COUNT, value);
    }

    // 中文：读取 tooltip 预览用的打击牌数量。
    // English: Reads the Strike-card count used by tooltip preview text.
    public static int getPreviewStrikeCount(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_STRIKE_COUNT) : 0;
    }

    // 中文：写入 tooltip 预览用的生命损失次数。
    // English: Stores the HP-loss count used by tooltip preview text.
    public static void setPreviewHpLoss(ItemStack stack, int value) {
        tag(stack).putInt(PREVIEW_HP_LOSS, value);
    }

    // 中文：读取 tooltip 预览用的生命损失次数。
    // English: Reads the HP-loss count used by tooltip preview text.
    public static int getPreviewHpLoss(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(PREVIEW_HP_LOSS) : 0;
    }

    // 中文：写入 tooltip 预览用的腐化状态。
    // English: Stores the Corruption state used by tooltip preview text.
    public static void setPreviewCorruption(ItemStack stack, boolean value) {
        tag(stack).putBoolean(PREVIEW_CORRUPTION, value);
    }

    // 中文：读取 tooltip 预览用的腐化状态。
    // English: Reads the Corruption state used by tooltip preview text.
    public static boolean isPreviewCorruption(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(PREVIEW_CORRUPTION);
    }

    // 中文：标记物品栈是否为当前手牌中的卡牌。
    // English: Marks whether the stack is a card currently in hand.
    public static void setInHand(ItemStack stack, boolean value) {
        tag(stack).putBoolean(IN_HAND, value);
    }

    // 中文：读取物品栈是否为当前手牌中的卡牌。
    // English: Reads whether the stack is a card currently in hand.
    public static boolean isInHand(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(IN_HAND);
    }

    // 中文：标记物品栈是否被消耗效果影响。
    // English: Marks whether the stack is affected by an exhaust effect.
    public static void setExhausted(ItemStack stack, boolean value) {
        tag(stack).putBoolean(EXHAUSTED, value);
    }

    // 中文：读取物品栈是否被消耗效果影响。
    // English: Reads whether the stack is affected by an exhaust effect.
    public static boolean isExhausted(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(EXHAUSTED);
    }

    // 中文：清理一场战斗结束后不应保留在物品栈上的临时标记。
    // English: Clears temporary stack flags that should not survive after a combat encounter.
    public static void clearCombatFlags(ItemStack stack) {
        if (!stack.hasTag()) {
            return;
        }

        // 中文：战斗结束清除单场战斗状态；非临时生成卡会移除 generated 标记，临时生成卡由 CombatService 直接移除。
        // English: End of combat clears per-encounter state; non-temporary cards lose generated marks, while temporary generated cards are removed by CombatService.
        stack.getTag().remove(USED_THIS_COMBAT);
        stack.getTag().remove(COST_OVERRIDE);
        stack.getTag().remove(IN_HAND);
        stack.getTag().remove(EXHAUSTED);
        if (!isGenerated(stack)) {
            stack.getTag().remove(GENERATED);
        }
        if (stack.getTag().isEmpty()) {
            stack.setTag(null);
        }
    }

    // 中文：获取或创建物品栈 NBT 根节点。
    // English: Gets or creates the root NBT tag for the stack.
    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrCreateTag();
    }
}
