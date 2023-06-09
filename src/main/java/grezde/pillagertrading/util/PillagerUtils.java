package grezde.pillagertrading.util;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.entity.PillagerTradeGoal;
import grezde.pillagertrading.entity.capability.PillagerTradeCapability;
import grezde.pillagertrading.entity.capability.PillagerTradeCapabilityProvider;
import grezde.pillagertrading.items.PTItems;
import grezde.pillagertrading.network.PTPackets;
import grezde.pillagertrading.network.PillagerPoseSyncPacket;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public class PillagerUtils {

    public static PillagerTradeGoal getGoal(LivingEntity pillager) {
        return (PillagerTradeGoal) ((Pillager)pillager).goalSelector.getAvailableGoals().stream().filter(goal -> goal.getGoal() instanceof PillagerTradeGoal).toList().get(0).getGoal();
    }

    private static InteractionHand getOrderHand(Player player) {
        if(player.getItemInHand(InteractionHand.OFF_HAND).is(PTItems.ILLAGER_ORDER.get()))
            return InteractionHand.OFF_HAND;
        else
            return InteractionHand.MAIN_HAND;
    }

    private static InteractionHand getPriceHand(Player player) {
        return getOrderHand(player) == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public static ItemStack getPrice(Player player) {
        return player.getItemInHand(getPriceHand(player));
    }

    public static PillagerTradingRecipe getRecipe(Player player) {
        Optional<? extends Recipe<?>> maybeRecipe = player.level.getRecipeManager().byKey(new ResourceLocation(player.getItemInHand(getOrderHand(player)).getTag().getString("recipe")));
        return (PillagerTradingRecipe) maybeRecipe.get();
    }

    public static boolean hasCapability(LivingEntity pillager) {
        return pillager.getCapability(PillagerTradeCapabilityProvider.PILLAGER_TRADE_COOLDOWN).resolve().isPresent();
    }

    public static PillagerTradeCapability getCapability(LivingEntity pillager) {
        return pillager.getCapability(PillagerTradeCapabilityProvider.PILLAGER_TRADE_COOLDOWN).resolve().get();
    }

    public static boolean shouldPillagerDisplay(PillagerTradingRecipe ptr, ItemStack input) {
        if(input.isEmpty())
            return false;
        return input.is(ptr.getInput().getItem());
    }

    public static boolean shouldPillagerTrade(PillagerTradingRecipe ptr, ItemStack input) {
        return ptr.getInput().getCount() <= input.getCount() && shouldPillagerDisplay(ptr, input);
    }

    public static boolean shouldHaveTradeAI(Pillager pillager) {
        boolean Angree = hasCapability(pillager) ? !getCapability(pillager).belowAngerLimit() : false;
        return !Angree && pillager.getPersistentData().get("RaidId") == null;
    }

    public static boolean hasTradeAI(Pillager pillager) {
        return pillager.goalSelector.getAvailableGoals().stream().anyMatch((goal) -> goal.getGoal() instanceof PillagerTradeGoal);
    }

    public static void destroyTradeAI(Pillager pillager) {
        PillagerTradeGoal goal = getGoal(pillager);
        goal.stop();
        PTPackets.sendToAllPlayers(new PillagerPoseSyncPacket(false, pillager));
        pillager.goalSelector.removeGoal(goal);
    }

}
