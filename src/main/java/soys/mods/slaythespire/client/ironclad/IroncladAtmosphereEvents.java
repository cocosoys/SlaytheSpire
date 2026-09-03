package soys.mods.slaythespire.client.ironclad;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.equipment.IroncladSet;

/**
 * 中文：铁甲战士套装的轻量客户端氛围效果。只生成本地粒子，不写入服务器或战斗状态。
 * English: Lightweight client atmosphere effect for the Ironclad set. It only spawns local particles and writes no server or combat state.
 */
@Mod.EventBusSubscriber(modid = Slaythespire.MODID, value = Dist.CLIENT)
public final class IroncladAtmosphereEvents {
    private IroncladAtmosphereEvents() {
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || !IroncladSet.isWearingFullSet(minecraft.player)) {
            return;
        }
        if (minecraft.player.tickCount % 30 != 0) {
            return;
        }
        // 中文：火星粒子用于提示外观激活，不能改变力量、格挡、能量或任何战斗数值。
        // English: Flame particles hint that the appearance is active and cannot change strength, block, energy, or any combat value.
        minecraft.level.addParticle(ParticleTypes.FLAME,
                minecraft.player.getX(),
                minecraft.player.getY() + 1.1D,
                minecraft.player.getZ(),
                0.0D, 0.03D, 0.0D);
    }
}
