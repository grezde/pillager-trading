package grezde.pillagertrading.entity.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PillagerTradeCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PillagerTradeCapability> PILLAGER_TRADE_COOLDOWN = CapabilityManager.get(new CapabilityToken<PillagerTradeCapability>() {
    });

    private PillagerTradeCapability ptcooldown = null;
    private final LazyOptional<PillagerTradeCapability> optional = LazyOptional.of(this::createPTC);

    private PillagerTradeCapability createPTC() {
        if(ptcooldown == null)
           ptcooldown = new PillagerTradeCapability();
        return ptcooldown;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PILLAGER_TRADE_COOLDOWN)
            return optional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPTC().saveToNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPTC().loadFromNBT(nbt);
    }
}
