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
        return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        optional.invalidate();
    }
}
