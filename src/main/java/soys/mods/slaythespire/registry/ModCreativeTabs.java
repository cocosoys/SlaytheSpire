package soys.mods.slaythespire.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardDefinitions;

/**
 * 中文：创造模式标签页注册。卡牌页保持现有红色卡牌，收藏页集中展示铁甲战士套装、遗物和药水收藏品。
 * English: Creative tab registration. The card tab keeps current red cards, while the collection tab displays the Ironclad set, relics, and potion collectibles.
 */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Slaythespire.MODID);

    public static final RegistryObject<CreativeModeTab> CARDS = TABS.register("cards", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.slaythespire.cards"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.createCardStack(CardDefinitions.STRIKE.id()))
            .displayItems((parameters, output) -> {
                // 中文：展示顺序沿用 CardDefinitions.playableRedCards()，方便和卡牌定义顺序对应。
                // English: Display order follows CardDefinitions.playableRedCards(), keeping it aligned with definition order.
                for (CardDefinition definition : CardDefinitions.playableRedCards()) {
                    output.accept(ModItems.createCardStack(definition.id()));
                }
            })
            .build());

    public static final RegistryObject<CreativeModeTab> COLLECTIONS = TABS.register("collections", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.slaythespire.collections"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.IRONCLAD_HELMET.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (RegistryObject<net.minecraft.world.item.Item> item : ModItems.ironcladEquipment()) {
                    output.accept(item.get());
                }
                for (RegistryObject<net.minecraft.world.item.Item> item : ModItems.relicCollectibles()) {
                    output.accept(item.get());
                }
                for (RegistryObject<net.minecraft.world.item.Item> item : ModItems.potionCollectibles()) {
                    output.accept(item.get());
                }
            })
            .build());

    private ModCreativeTabs() {
    }
}
