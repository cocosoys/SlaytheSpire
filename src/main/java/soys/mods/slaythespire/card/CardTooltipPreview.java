package soys.mods.slaythespire.card;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CardTooltipPreview {
    private CardTooltipPreview() {
    }

    public static String displayCost(CardDefinition definition, ItemStack stack) {
        int cost = definition.cost();
        Integer override = CardStacks.getCostOverride(stack);
        if (override != null) {
            cost = override;
        }
        return Integer.toString(cost);
    }

    public static void appendPreviewLines(CardDefinition definition, ItemStack stack, List<Component> tooltip) {
        int strength = CardStacks.getPreviewStrength(stack);

        String id = definition.effectKey();
        switch (id) {
            case "strike_card" -> addDamage(tooltip, 6 + strength);
            case "defend_card" -> addBlock(tooltip, 5);
            default -> {
            }
        }
    }

    private static void addDamage(List<Component> tooltip, int damage) {
        tooltip.add(Component.translatable("tooltip.slaythespire.current_damage", damage).withStyle(ChatFormatting.GREEN));
    }

    private static void addBlock(List<Component> tooltip, int block) {
        tooltip.add(Component.translatable("tooltip.slaythespire.current_block", block).withStyle(ChatFormatting.BLUE));
    }
}
