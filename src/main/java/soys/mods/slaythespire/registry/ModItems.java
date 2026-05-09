package soys.mods.slaythespire.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardDefinitions;
import soys.mods.slaythespire.card.CardItem;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 中文：物品注册表。每个 Java CardDefinition 自动注册成一个 CardItem，保持卡牌定义和物品注册一一对应。
 * English: Item registry. Each Java CardDefinition is automatically registered as a CardItem, keeping definitions and item registrations one-to-one.
 */
public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Slaythespire.MODID);
    // 中文：按卡牌 id 保存 RegistryObject，生成临时卡或创造模式列表时可以反查物品。
    // English: RegistryObjects are stored by card id so generated cards and creative-tab listing can look up the item.
    private static final Map<ResourceLocation, RegistryObject<Item>> CARD_ITEMS = new LinkedHashMap<>();

    static {
        // 中文：这里是统一 Java API 的关键：新增 CardDefinitions 后无需手写新的 ITEMS.register。
        // English: This is the key to the unified Java API: adding CardDefinitions does not require a hand-written ITEMS.register call.
        for (CardDefinition definition : CardDefinitions.all()) {
            CARD_ITEMS.put(definition.id(), ITEMS.register(definition.id().getPath(), () -> new CardItem(definition.id())));
        }
    }

    private ModItems() {
    }

    public static ItemStack createCardStack(ResourceLocation id) {
        // 中文：未知卡牌 id 返回空栈，调用方可据此跳过生成。
        // English: Unknown card ids return an empty stack so callers can skip generation safely.
        RegistryObject<Item> item = CARD_ITEMS.get(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item.get());
    }

    public static Iterable<RegistryObject<Item>> redCards() {
        return CARD_ITEMS.values();
    }
}
