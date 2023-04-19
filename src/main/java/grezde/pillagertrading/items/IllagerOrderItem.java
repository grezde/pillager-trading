package grezde.pillagertrading.items;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class IllagerOrderItem extends Item {

    public IllagerOrderItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }

    private MutableComponent genItemTooltip(ItemStack stack) {
        MutableComponent a;
        if(stack.getItem().getMaxStackSize(stack) == 1)
            a = stack.getItem().getName(stack).copy();
        else {
            a = Component.literal(stack.getCount() + "x ");
            a.append(stack.getItem().getName(stack));
        }
        a.setStyle(a.getStyle().applyFormat(ChatFormatting.DARK_AQUA));
        return a;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        PTMod.LOGGER.info("HELLO FROM FIRST TOOLTIP DISPLAY");
        if(level == null)
            return;
        String s = stack.getTag().getString("recipe");
        if(s == null)
            return;
        ResourceLocation rl = new ResourceLocation(s);
        if(rl == null)
            return;
        Optional<? extends Recipe<?>> r = level.getRecipeManager().byKey(rl);
        if(r.isPresent() && r.get() instanceof PillagerTradingRecipe ptr) {
            PTMod.LOGGER.info("HELLO FROM SECOND TOOLTIP DISPLAY");
            MutableComponent give = Component.translatable("gui.pillagertrading.order_give");
            MutableComponent recieve = Component.translatable("gui.pillagertrading.order_receive");
            give.setStyle(give.getStyle().applyFormat(ChatFormatting.GRAY));
            recieve.setStyle(recieve.getStyle().applyFormat(ChatFormatting.GRAY));
            give.append(genItemTooltip(ptr.getInput()));
            recieve.append(genItemTooltip(ptr.getResultItem()));
            components.add(give);
            components.add(recieve);
        }
        super.appendHoverText(stack, level, components, tooltipFlag);
    }
}
