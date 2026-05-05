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
        return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        optional.invalidate();
    }
}
