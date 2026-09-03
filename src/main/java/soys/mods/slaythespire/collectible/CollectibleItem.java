package soys.mods.slaythespire.collectible;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * 中文：遗物和药水的非战斗收藏物品。它只提供贴图、提示和客户端氛围反馈，不调用卡牌或战斗系统。
 * English: Non-combat collectible item for relics and potions. It only provides art, tooltips, and client atmosphere feedback.
 */
public class CollectibleItem extends Item {
    private final CollectibleDefinition definition;

    public CollectibleItem(CollectibleDefinition definition) {
        super(new Item.Properties().stacksTo(64));
        this.definition = definition;
    }

    /**
     * 中文：返回静态收藏定义，测试和创造标签页会用它确认注册数量与类型。
     * English: Returns the static collectible definition for registry-count checks and creative tab grouping.
     */
    public CollectibleDefinition definition() {
        return definition;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String kindKey = definition.kind() == CollectibleKind.RELIC
                ? "tooltip.slaythespire.collectible.relic"
                : "tooltip.slaythespire.collectible.potion";
        tooltip.add(Component.translatable(kindKey).withStyle(ChatFormatting.GRAY));
        boolean hasEffect = definition.kind() == CollectibleKind.RELIC
                ? CollectibleEffects.hasRelicEffect(definition.id())
                : CollectibleEffects.hasPotionEffect(definition.id());
        if (hasEffect) {
            tooltip.add(Component.translatable("tooltip.slaythespire.collectible.active").withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(Component.translatable("tooltip.slaythespire.collectible.non_combat").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            // 中文：右键收藏品播放轻量氛围反馈。
            // English: Right-clicking a collectible plays light atmosphere feedback.
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.25F, 1.45F, false);
            for (int i = 0; i < 6; i++) {
                double angle = (Math.PI * 2.0D / 6.0D) * i;
                level.addParticle(ParticleTypes.END_ROD,
                        player.getX() + Math.cos(angle) * 0.45D,
                        player.getY() + 1.0D,
                        player.getZ() + Math.sin(angle) * 0.45D,
                        0.0D, 0.03D, 0.0D);
            }
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        // 中文：药水在服务端触发效果并消耗一瓶；遗物为被动效果，右键不触发。
        // English: Potions trigger their effect on the server and consume one dose; relics are passive and do not trigger on right-click.
        if (definition.kind() == CollectibleKind.POTION && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            CollectibleEffect effect = CollectibleEffects.getPotionEffect(definition.id());
            if (effect != null) {
                soys.mods.slaythespire.combat.CombatState state = soys.mods.slaythespire.combat.CombatStateAccess.get(serverPlayer);
                effect.apply(new CollectibleEffect.Context(serverPlayer, state, null, stack));
                stack.shrink(1);
                soys.mods.slaythespire.network.ModNetworking.sync(serverPlayer);
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        try {
            // 中文：通过反射接入客户端渲染扩展，避免 dedicated server 类加载 client 包。
            // English: Loads the client rendering extension reflectively so dedicated servers do not load client packages.
            Class<?> extensions = Class.forName("soys.mods.slaythespire.client.collectible.CollectibleItemExtensions");
            consumer.accept((IClientItemExtensions) extensions.getField("INSTANCE").get(null));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to initialize collectible renderer", exception);
        }
    }
}
