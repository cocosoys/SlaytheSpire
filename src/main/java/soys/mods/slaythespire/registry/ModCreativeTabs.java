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

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Slaythespire.MODID);

    public static final RegistryObject<CreativeModeTab> CARDS = TABS.register("cards", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.slaythespire.cards"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.createCardStack(CardDefinitions.STRIKE.id()))
            .displayItems((parameters, output) -> {
                for (CardDefinition definition : CardDefinitions.playableRedCards()) {
                    output.accept(ModItems.createCardStack(definition.id()));
                }
            })
            .build());

    private ModCreativeTabs() {
    }
}
