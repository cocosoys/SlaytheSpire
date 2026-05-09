package soys.mods.slaythespire.card;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 中文：tooltip 动态预览工具。它把当前战斗状态写入 ItemStack 后，在 tooltip 中显示会随力量、费用覆盖等变化的数值。
 * English: Dynamic tooltip preview helper. It reads combat-derived values stored on ItemStack and shows numbers affected by strength, cost overrides, and similar state.
 */
public final class CardTooltipPreview {
    // 中文：禁止实例化纯静态 tooltip 预览工具类。
    // English: Prevents instantiation of this static-only tooltip preview utility.
    private CardTooltipPreview() {
    }

    // 中文：计算 tooltip 中显示的费用文本，优先使用物品栈上的临时费用覆盖。
    // English: Computes the cost text shown in tooltips, preferring a temporary stack cost override.
    public static String displayCost(CardDefinition definition, ItemStack stack) {
        // 中文：X 费或临时费用变化会写入 cost override，显示时优先使用覆盖值。
        // English: X-cost or temporary cost changes are stored as a cost override, which takes priority for display.
        int cost = definition.cost();
        Integer override = CardStacks.getCostOverride(stack);
        if (override != null) {
            cost = override;
        }
        return Integer.toString(cost);
    }

    // 中文：根据卡牌定义和物品栈状态追加动态预览行。
    // English: Appends dynamic preview lines based on card definition and stack state.
    public static void appendPreviewLines(CardDefinition definition, ItemStack stack, List<Component> tooltip) {
        // 中文：当前只为已迁移基础样例牌补动态预览；后续卡牌可在这里继续追加分支。
        // English: Dynamic preview is currently implemented for migrated sample cards; future cards can add branches here.
        int strength = CardStacks.getPreviewStrength(stack);

        String id = definition.effectKey();
        switch (id) {
            case "strike_card" -> addDamage(tooltip, 6 + strength);
            case "defend_card" -> addBlock(tooltip, 5);
            default -> {
            }
        }
    }

    // 中文：追加当前伤害预览行。
    // English: Appends the current-damage preview line.
    private static void addDamage(List<Component> tooltip, int damage) {
        tooltip.add(Component.translatable("tooltip.slaythespire.current_damage", damage).withStyle(ChatFormatting.GREEN));
    }

    // 中文：追加当前格挡预览行。
    // English: Appends the current-block preview line.
    private static void addBlock(List<Component> tooltip, int block) {
        tooltip.add(Component.translatable("tooltip.slaythespire.current_block", block).withStyle(ChatFormatting.BLUE));
    }
}
