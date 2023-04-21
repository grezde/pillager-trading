package grezde.pillagertrading.entity.capability;

import grezde.pillagertrading.util.PTConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class PillagerTradeCapability {

    private final int SOUND_COOLDOWN = 10;
    private int cooldownTicks;
    private int soundTicks;
    public ItemStack initialOffHand;
    public boolean changedHands;

    public PillagerTradeCapability() {
        this.cooldownTicks = 0;
        this.soundTicks = 0;
        this.initialOffHand = ItemStack.EMPTY;
        this.changedHands = false;
    }

    public void tick() {
        if(soundTicks > 0)
            soundTicks--;
        if(cooldownTicks > 0)
            cooldownTicks--;
    }

    public boolean canTrade() {
        return cooldownTicks <= 0;
    }

    public boolean belowAngerLimit() { return cooldownTicks <= PTConfig.TRADING_COOLDOWN.get(); }

    public void reset(int level) {
        this.cooldownTicks = level == 1 ? PTConfig.TRADING_COOLDOWN.get() : (PTConfig.TRADING_COOLDOWN.get()+10);
    }

    public boolean canPlaySound() {
        return soundTicks <= 0;
    }

    public void resetSound() { soundTicks = SOUND_COOLDOWN; }

    public void saveToNBT(CompoundTag nbt) {
        nbt.putInt("tradeCooldown", cooldownTicks);
        nbt.putInt("soundCooldown", soundTicks);
        nbt.putBoolean("changedHands", changedHands);
        CompoundTag itemTag = new CompoundTag();
        initialOffHand.save(itemTag);
        nbt.put("item", itemTag);
    }

    public void loadFromNBT(CompoundTag nbt) {
        this.cooldownTicks = nbt.getInt("tradeCooldown");
        this.soundTicks = nbt.getInt("soundCooldown");
        this.changedHands = nbt.getBoolean("changedHands");
        this.initialOffHand = ItemStack.of(nbt.getCompound("item"));
    }

}
