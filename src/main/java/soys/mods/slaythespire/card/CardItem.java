package soys.mods.slaythespire.card;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import soys.mods.slaythespire.combat.CombatService;

import java.util.List;
import java.util.function.Consumer;

public class CardItem extends Item {
    private final ResourceLocation cardId;

    public CardItem(ResourceLocation cardId) {
        super(new Item.Properties().stacksTo(1));
        this.cardId = cardId;
    }

    public CardDefinition definition() {
        return CardDefinitions.require(cardId);
    }

    public @Nullable CardDefinition definition(ItemStack stack, @Nullable Level level) {
        return resolveDefinition(stack, level);
    }

    protected CardDefinition resolveDefinition(ItemStack stack, @Nullable Level level) {
        return definition();
    }

    @Override
    public Component getName(ItemStack stack) {
        CardDefinition definition = resolveDefinition(stack, null);
        return definition != null ? definition.displayName() : super.getName(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CardDefinition definition = resolveDefinition(stack, level);
        if (definition == null) {
            return InteractionResultHolder.fail(stack);
        }

        if (definition.target() == CardTarget.ENEMY) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.slaythespire.need_enemy_target"), true);
            }

            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            boolean success = CombatService.tryUseCard(serverPlayer, definition, null, stack);
            return success ? InteractionResultHolder.sidedSuccess(stack, false) : InteractionResultHolder.fail(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, true);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        CardDefinition definition = resolveDefinition(stack, player.level());
        if (definition == null) {
            return InteractionResult.FAIL;
        }
        if (definition.target() != CardTarget.ENEMY) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            boolean success = CombatService.tryUseCard(serverPlayer, definition, interactionTarget, stack);
            return success ? InteractionResult.sidedSuccess(false) : InteractionResult.FAIL;
        }

        return InteractionResult.sidedSuccess(true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CardDefinition definition = resolveDefinition(stack, level);
        if (definition == null) {
            return;
        }
        String costText = CardTooltipPreview.displayCost(definition, stack);
        tooltip.add(Component.translatable("tooltip.slaythespire.card_cost", costText).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.slaythespire.card_rarity", rarityText(definition)).withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.translatable("tooltip.slaythespire.card_type", typeText(definition)).withStyle(ChatFormatting.GOLD));
        tooltip.add(definition.description().copy().withStyle(ChatFormatting.GRAY));
        CardTooltipPreview.appendPreviewLines(definition, stack, tooltip);
        String targetKey = switch (definition.target()) {
            case SELF -> "tooltip.slaythespire.self_target";
            case ENEMY -> "tooltip.slaythespire.enemy_target";
            case ALL_ENEMIES -> "tooltip.slaythespire.all_enemies_target";
            case NONE -> "tooltip.slaythespire.no_target";
        };
        tooltip.add(Component.translatable(targetKey).withStyle(ChatFormatting.DARK_GRAY));
        if (definition.exhausts()) {
            tooltip.add(Component.translatable("tooltip.slaythespire.exhausts").withStyle(ChatFormatting.RED));
        }
        if (definition.ethereal()) {
            tooltip.add(Component.translatable("tooltip.slaythespire.ethereal").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    private static Component rarityText(CardDefinition definition) {
        return switch (definition.rarity()) {
            case BASIC -> Component.translatable("card.slaythespire.rarity.basic");
            case COMMON -> Component.translatable("card.slaythespire.rarity.common");
            case UNCOMMON -> Component.translatable("card.slaythespire.rarity.uncommon");
            case RARE -> Component.translatable("card.slaythespire.rarity.rare");
            case STATUS -> Component.translatable("card.slaythespire.rarity.status");
        };
    }

    private static Component typeText(CardDefinition definition) {
        return switch (definition.type()) {
            case ATTACK -> Component.translatable("card.slaythespire.type.attack");
            case SKILL -> Component.translatable("card.slaythespire.type.skill");
            case POWER -> Component.translatable("card.slaythespire.type.power");
            case STATUS -> Component.translatable("card.slaythespire.type.status");
        };
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        try {
            Class<?> extensions = Class.forName("soys.mods.slaythespire.client.card.SlayCardItemExtensions");
            consumer.accept((IClientItemExtensions) extensions.getField("INSTANCE").get(null));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to initialize card renderer", exception);
        }
    }
}
