package grezde.pillagertrading.entity.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class PillagerTradeCapability {

    private final int COOLDOWN = 1200; // 1 minute at normal tps
    private final int HURT_COOLDOWN = 100; // 5 seconds
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

    public boolean belowAngerLimit() { return cooldownTicks <= COOLDOWN; }

    public void reset(int level) {
        this.cooldownTicks = level == 1 ? COOLDOWN : (COOLDOWN+HURT_COOLDOWN);
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
