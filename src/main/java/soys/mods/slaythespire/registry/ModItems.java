package soys.mods.slaythespire.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardDefinitions;
import soys.mods.slaythespire.card.CardItem;
import soys.mods.slaythespire.collectible.CollectibleDefinition;
import soys.mods.slaythespire.collectible.CollectibleDefinitions;
import soys.mods.slaythespire.collectible.CollectibleItem;
import soys.mods.slaythespire.equipment.IroncladArmorItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 中文：物品注册表。卡牌、铁甲战士外观套装、遗物收藏品和药水收藏品都从静态 Java 定义注册。
 * English: Item registry. Cards, the Ironclad appearance set, relic collectibles, and potion collectibles are registered from static Java definitions.
 */
public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Slaythespire.MODID);

    public static final RegistryObject<Item> IRONCLAD_HELMET = ITEMS.register("ironclad_helmet",
            () -> new IroncladArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> IRONCLAD_CHESTPLATE = ITEMS.register("ironclad_chestplate",
            () -> new IroncladArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> IRONCLAD_LEGGINGS = ITEMS.register("ironclad_leggings",
            () -> new IroncladArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> IRONCLAD_BOOTS = ITEMS.register("ironclad_boots",
            () -> new IroncladArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

    // 中文：按卡牌 id 保存 RegistryObject，生成临时卡或创造模式列表时可以反查物品。
    // English: RegistryObjects are stored by card id so generated cards and creative-tab listing can look up the item.
    private static final Map<ResourceLocation, RegistryObject<Item>> CARD_ITEMS = new LinkedHashMap<>();
    // 中文：收藏物品按生成清单注册，运行时不扫描资源目录。
    // English: Collectibles are registered from the generated manifest, with no runtime directory scan.
    private static final Map<ResourceLocation, RegistryObject<Item>> COLLECTIBLE_ITEMS = new LinkedHashMap<>();

    static {
        // 中文：这里是统一 Java API 的关键：新增 CardDefinitions 后无需手写新的 ITEMS.register。
        // English: This is the key to the unified Java API: adding CardDefinitions does not require a hand-written ITEMS.register call.
        for (CardDefinition definition : CardDefinitions.all()) {
            CARD_ITEMS.put(definition.id(), ITEMS.register(definition.id().getPath(), () -> new CardItem(definition.id())));
        }
        for (CollectibleDefinition definition : CollectibleDefinitions.all()) {
            COLLECTIBLE_ITEMS.put(definition.id(), ITEMS.register(definition.id().getPath(), () -> new CollectibleItem(definition)));
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

    public static ItemStack createCollectionStack(ResourceLocation id) {
        RegistryObject<Item> item = COLLECTIBLE_ITEMS.get(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item.get());
    }

    public static Iterable<RegistryObject<Item>> redCards() {
        return CARD_ITEMS.values();
    }

    public static Iterable<RegistryObject<Item>> ironcladEquipment() {
        return List.of(IRONCLAD_HELMET, IRONCLAD_CHESTPLATE, IRONCLAD_LEGGINGS, IRONCLAD_BOOTS);
    }

    public static Iterable<RegistryObject<Item>> relicCollectibles() {
        return collectiblesFor(CollectibleDefinitions.RELICS);
    }

    public static Iterable<RegistryObject<Item>> potionCollectibles() {
        return collectiblesFor(CollectibleDefinitions.POTIONS);
    }

    public static Iterable<RegistryObject<Item>> collectibles() {
        return COLLECTIBLE_ITEMS.values();
    }

    private static Iterable<RegistryObject<Item>> collectiblesFor(List<CollectibleDefinition> definitions) {
        ArrayList<RegistryObject<Item>> items = new ArrayList<>(definitions.size());
        for (CollectibleDefinition definition : definitions) {
            RegistryObject<Item> item = COLLECTIBLE_ITEMS.get(definition.id());
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
}
