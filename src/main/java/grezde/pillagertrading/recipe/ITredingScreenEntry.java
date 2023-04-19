package grezde.pillagertrading.recipe;

import net.minecraft.world.item.ItemStack;

public interface ITredingScreenEntry {

    public ItemStack getTradingInitialFirstItem();

    public ItemStack getTradingFirstItem();

    public ItemStack getTradingSecondItem();

    public ItemStack getTradingResult();

    public boolean isTradeCrossed();

}
