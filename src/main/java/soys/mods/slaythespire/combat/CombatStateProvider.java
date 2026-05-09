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
 * 中文：玩家战斗状态 Capability 提供者。每个玩家实体持有一份 CombatState。
 * English: Capability provider for player combat state. Each player entity owns one CombatState instance.
 */
public final class CombatStateProvider implements ICapabilityProvider {
    public static final Capability<CombatState> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final CombatState state = new CombatState();
    private final LazyOptional<CombatState> optional = LazyOptional.of(() -> state);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(CombatState.class);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 中文：该 Capability 不区分方向，任何 side 查询同一个玩家战斗状态。
        // English: This Capability is side-independent; every side query returns the same player combat state.
        return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        optional.invalidate();
    }
}
