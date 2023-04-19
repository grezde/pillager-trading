package grezde.pillagertrading.items;

import grezde.pillagertrading.client.gui.IllagerManuscriptScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IllagerManuscriptItem extends Item {

    public IllagerManuscriptItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(level.isClientSide() && player instanceof LocalPlayer localPlayer) {
            player.playSound(SoundEvents.BOOK_PAGE_TURN);
            Minecraft.getInstance().setScreen(new IllagerManuscriptScreen(player, itemstack, hand));
        }
        else if(player instanceof ServerPlayer sp) {
            /*List<Pair<ItemStack, ItemStack>> recipes =
                level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == PillagerTradingRecipe.Type.INSTANCE)
                .map(recipe -> {
                    if(recipe instanceof PillagerTradingRecipe ptr)
                        return new Pair<>(ptr.getInput(), ptr.getResultItem());
                    return null;
                })
                .toList();*/
            //PTPackets.sendToPlayer(new SendPillagerTradingRecipesPacket(new ArrayList<>(recipes)), sp);
            //PTMod.LOGGER.info("GOOFIER AH LIST SIZE: " + level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == PillagerTradingRecipe.Type.INSTANCE).toList().size());
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
