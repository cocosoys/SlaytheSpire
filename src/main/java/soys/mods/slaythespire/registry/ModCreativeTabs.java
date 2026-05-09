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
 * 中文：创造模式标签页注册。当前把已迁移的红色牌集中展示在单独的“杀戮尖塔卡牌”页。
 * English: Creative tab registration. The current migrated red cards are shown together in a dedicated Slay the Spire card tab.
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

    private ModCreativeTabs() {
    }
}
