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

/**
 * 中文：卡牌物品入口。它把 Minecraft 右键/点实体/tooltip/客户端自定义渲染接入到 CardDefinition 和 CombatService。
 * English: Card item entrypoint. It connects Minecraft use, entity interaction, tooltip, and client custom rendering to CardDefinition and CombatService.
 */
public class CardItem extends Item {
    // 中文：物品只保存卡牌 id，真正的可玩数据从 CardDefinitions 读取，避免每个 Item 重复保存一份定义。
    // English: The item stores only the card id; playable data is read from CardDefinitions to avoid duplicating definitions in each Item.
    private final ResourceLocation cardId;

    // 中文：创建绑定指定卡牌定义 id 的物品实例。
    // English: Creates an item instance bound to the given card definition id.
    public CardItem(ResourceLocation cardId) {
        super(new Item.Properties().stacksTo(1));
        this.cardId = cardId;
    }

    // 中文：返回此物品默认绑定的卡牌定义。
    // English: Returns the default card definition bound to this item.
    public CardDefinition definition() {
        return CardDefinitions.require(cardId);
    }

    // 中文：解析指定物品栈在当前世界上下文中的卡牌定义。
    // English: Resolves the card definition for the given stack in the current level context.
    public @Nullable CardDefinition definition(ItemStack stack, @Nullable Level level) {
        return resolveDefinition(stack, level);
    }

    // 中文：提供定义解析覆盖点，允许未来按 ItemStack 状态切换卡牌定义。
    // English: Provides the definition-resolution hook for future stack-specific card variants.
    protected CardDefinition resolveDefinition(ItemStack stack, @Nullable Level level) {
        // 中文：保留覆盖点给未来动态卡牌或临时变体；当前实现直接返回 Java 注册定义。
        // English: This hook remains for future dynamic cards or temporary variants; the current implementation returns the Java-registered definition.
        return definition();
    }

    @Override
    // 中文：返回物品栈显示名，优先使用卡牌定义的本地化名称。
    // English: Returns the stack display name, preferring the localized name from the card definition.
    public Component getName(ItemStack stack) {
        CardDefinition definition = resolveDefinition(stack, null);
        return definition != null ? definition.displayName() : super.getName(stack);
    }

    @Override
    // 中文：处理右键使用自身目标卡牌，服务端负责真实结算。
    // English: Handles right-click use for self-target cards, with real settlement on the server.
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CardDefinition definition = resolveDefinition(stack, level);
        if (definition == null) {
            return InteractionResultHolder.fail(stack);
        }

        if (definition.target() == CardTarget.ENEMY) {
            // 中文：需要敌方目标的卡牌不能空放，必须通过 interactLivingEntity 传入具体目标。
            // English: Enemy-target cards cannot be used into empty space; interactLivingEntity must provide the concrete target.
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.slaythespire.need_enemy_target"), true);
            }

            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            // 中文：真正的费用、战斗状态和效果结算只在服务端执行，客户端只返回交互成功以保持手感。
            // English: Real cost, combat state, and effect settlement run only on the server; the client only reports interaction success for responsiveness.
            boolean success = CombatService.tryUseCard(serverPlayer, definition, null, stack);
            return success ? InteractionResultHolder.sidedSuccess(stack, false) : InteractionResultHolder.fail(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, true);
    }

    @Override
    // 中文：处理右键实体使用敌方目标卡牌。
    // English: Handles entity interaction for enemy-target cards.
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        CardDefinition definition = resolveDefinition(stack, player.level());
        if (definition == null) {
            return InteractionResult.FAIL;
        }
        if (definition.target() != CardTarget.ENEMY) {
            // 中文：非敌方目标卡牌交还给普通交互链，避免阻断 Minecraft 原有实体右键行为。
            // English: Non-enemy-target cards pass through to the normal interaction chain so vanilla entity interactions are not blocked.
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            boolean success = CombatService.tryUseCard(serverPlayer, definition, interactionTarget, stack);
            return success ? InteractionResult.sidedSuccess(false) : InteractionResult.FAIL;
        }

        return InteractionResult.sidedSuccess(true);
    }

    @Override
    // 中文：追加卡牌费用、稀有度、类型、描述和预览数值 tooltip。
    // English: Appends card cost, rarity, type, description, and preview-value tooltips.
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CardDefinition definition = resolveDefinition(stack, level);
        if (definition == null) {
            return;
        }
        String costText = CardTooltipPreview.displayCost(definition, stack);
        // 中文：tooltip 全部走翻译键，和牌面文字使用同一套语言资源。
        // English: Tooltips use translation keys throughout, sharing the same language resources as card-face text.
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

    // 中文：把卡牌稀有度转换为本地化 tooltip 文本。
    // English: Converts card rarity into localized tooltip text.
    private static Component rarityText(CardDefinition definition) {
        return switch (definition.rarity()) {
            case BASIC -> Component.translatable("card.slaythespire.rarity.basic");
            case COMMON -> Component.translatable("card.slaythespire.rarity.common");
            case UNCOMMON -> Component.translatable("card.slaythespire.rarity.uncommon");
            case RARE -> Component.translatable("card.slaythespire.rarity.rare");
            case STATUS -> Component.translatable("card.slaythespire.rarity.status");
        };
    }

    // 中文：把卡牌类型转换为本地化 tooltip 文本。
    // English: Converts card type into localized tooltip text.
    private static Component typeText(CardDefinition definition) {
        return switch (definition.type()) {
            case ATTACK -> Component.translatable("card.slaythespire.type.attack");
            case SKILL -> Component.translatable("card.slaythespire.type.skill");
            case POWER -> Component.translatable("card.slaythespire.type.power");
            case STATUS -> Component.translatable("card.slaythespire.type.status");
        };
    }

    @Override
    // 中文：注册客户端自定义物品渲染扩展。
    // English: Registers the client-side custom item rendering extension.
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        try {
            // 中文：通过反射加载客户端扩展，避免服务端类加载时直接引用 client 包导致 dedicated server 崩溃。
            // English: Client extensions are loaded reflectively so dedicated servers do not load client-only classes directly.
            Class<?> extensions = Class.forName("soys.mods.slaythespire.client.card.SlayCardItemExtensions");
            consumer.accept((IClientItemExtensions) extensions.getField("INSTANCE").get(null));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to initialize card renderer", exception);
        }
    }
}
