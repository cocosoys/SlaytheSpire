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

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Slaythespire.MODID);
    private static final Map<ResourceLocation, RegistryObject<Item>> CARD_ITEMS = new LinkedHashMap<>();

    static {
        for (CardDefinition definition : CardDefinitions.all()) {
            CARD_ITEMS.put(definition.id(), ITEMS.register(definition.id().getPath(), () -> new CardItem(definition.id())));
        }
    }

    private ModItems() {
    }

    public static ItemStack createCardStack(ResourceLocation id) {
        RegistryObject<Item> item = CARD_ITEMS.get(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item.get());
    }

    public static Iterable<RegistryObject<Item>> redCards() {
        return CARD_ITEMS.values();
    }
}
