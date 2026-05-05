package soys.mods.slaythespire;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import soys.mods.slaythespire.combat.CombatantStatusProvider;
import soys.mods.slaythespire.combat.CombatEvents;
import soys.mods.slaythespire.combat.CombatStateProvider;
import soys.mods.slaythespire.network.ModNetworking;
import soys.mods.slaythespire.registry.ModCreativeTabs;
import soys.mods.slaythespire.registry.ModItems;

@Mod(Slaythespire.MODID)
public final class Slaythespire {
    public static final String MODID = "slaythespire";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Slaythespire() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(CombatStateProvider::registerCapabilities);
        modBus.addListener(CombatantStatusProvider::registerCapabilities);

        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        MinecraftForge.EVENT_BUS.register(new CombatEvents());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }
}
