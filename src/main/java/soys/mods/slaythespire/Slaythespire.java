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

/**
 * 中文：SlaytheSpire mod 公共入口。这里注册物品、创造模式标签页、Capability、网络和服务端战斗事件。
 * English: Common entrypoint for the SlaytheSpire mod. It registers items, creative tabs, Capabilities, networking, and server combat events.
 */
@Mod(Slaythespire.MODID)
public final class Slaythespire {
    public static final String MODID = "slaythespire";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 中文：构造模组入口并注册生命周期、物品、创造标签页和运行时事件。
    // English: Constructs the mod entrypoint and registers lifecycle hooks, items, creative tabs, and runtime events.
    public Slaythespire() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // 中文：mod bus 负责生命周期注册，Forge EVENT_BUS 负责运行时战斗事件。
        // English: The mod bus handles lifecycle registration, while Forge EVENT_BUS handles runtime combat events.
        modBus.addListener(this::commonSetup);
        modBus.addListener(CombatStateProvider::registerCapabilities);
        modBus.addListener(CombatantStatusProvider::registerCapabilities);

        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        MinecraftForge.EVENT_BUS.register(new CombatEvents());
    }

    // 中文：执行公共初始化阶段需要延迟到安全线程的注册逻辑。
    // English: Runs common setup registration work that must be delayed to Forge's safe thread.
    private void commonSetup(final FMLCommonSetupEvent event) {
        // 中文：网络注册排进 enqueueWork，确保在 Forge common setup 的线程安全阶段完成。
        // English: Networking registration is enqueued so it runs during Forge's thread-safe common setup stage.
        event.enqueueWork(ModNetworking::register);
    }
}
