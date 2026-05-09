package soys.mods.slaythespire.combat;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 中文：LivingEntity 战斗状态 Capability 提供者。敌人和玩家都通过它保存易伤、虚弱等临时状态。
 * English: Capability provider for LivingEntity combat statuses. Enemies and players use it to store vulnerable, weak, and similar temporary states.
 */
public final class CombatantStatusProvider implements ICapabilityProvider {
    public static final Capability<CombatantStatus> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final CombatantStatus status = new CombatantStatus();
    private final LazyOptional<CombatantStatus> optional = LazyOptional.of(() -> status);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(CombatantStatus.class);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 中文：状态不依赖方位，返回同一个 CombatantStatus 实例即可。
        // English: Status is not directional, so every side can receive the same CombatantStatus instance.
        return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        optional.invalidate();
    }
}
