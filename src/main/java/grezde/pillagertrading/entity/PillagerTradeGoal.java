package grezde.pillagertrading.entity;

import grezde.pillagertrading.entity.capability.PillagerTradeCapability;
import grezde.pillagertrading.items.PTItems;
import grezde.pillagertrading.network.PTPackets;
import grezde.pillagertrading.network.PillagerPoseSyncPacket;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import grezde.pillagertrading.util.PillagerUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class PillagerTradeGoal extends Goal {

    private static final TargetingConditions PT_RANGE = TargetingConditions.forNonCombat().range(7.0D).selector(livingEntity -> {
        if(livingEntity instanceof Player player) {
            boolean mainHand = player.getItemInHand(InteractionHand.MAIN_HAND).is(PTItems.ILLAGER_ORDER.get());
            boolean offHand = player.getItemInHand(InteractionHand.OFF_HAND).is(PTItems.ILLAGER_ORDER.get());
            boolean hasBadOmen = player.hasEffect(MobEffects.BAD_OMEN);
            boolean hasHOTV = player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
            if(hasBadOmen || hasHOTV)
                return false;
            return mainHand || offHand;
        }
        return false;
    });

    private static final float STOP_DISTANCE = 2.0f;

    private Pillager pillager;
    public Player tradePlayer;
    public boolean canTrade;

    public PillagerTradeGoal(Pillager mob) {
        super();
        this.canTrade = false;
        this.pillager = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        tradePlayer = pillager.level.getNearestPlayer(PT_RANGE, pillager);
        PillagerTradeCapability cooldown = PillagerUtils.getCapability(pillager);
        return tradePlayer != null; // && cooldown.belowAngerLimit();
    }

    @Override
    public void tick() {

        // general mob ai
        if(pillager.distanceToSqr(tradePlayer) < STOP_DISTANCE*STOP_DISTANCE)
            pillager.getNavigation().stop();
        pillager.getLookControl().setLookAt(tradePlayer);
        PillagerTradeCapability cooldown = PillagerUtils.getCapability(pillager);
        cooldown.tick();
        if(cooldown.belowAngerLimit())
            pillager.setTarget(null);

        ItemStack theItem = PillagerUtils.getPrice(tradePlayer);
        try {
            PillagerTradingRecipe recipe = PillagerUtils.getRecipe(tradePlayer);
            boolean itemShouldDisplay = PillagerUtils.shouldPillagerDisplay(recipe, theItem);
            boolean itemShouldTrade = PillagerUtils.shouldPillagerTrade(recipe, theItem);
            boolean cooldownShouldTrade = cooldown.canTrade();
            if (itemShouldDisplay && cooldownShouldTrade)
                pillager.setItemInHand(InteractionHand.OFF_HAND, recipe.getResultItem());
            else
                pillager.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            if (itemShouldTrade && cooldownShouldTrade) {
                canTrade = true;
            }
        }
        catch (Exception e) {
            canTrade = false;
        }

    }

    @Override
    public void start() {
        PillagerTradeCapability cooldown = PillagerUtils.getCapability(pillager);
        if(!cooldown.changedHands) {
            cooldown.initialOffHand = pillager.getItemInHand(InteractionHand.OFF_HAND);
            cooldown.changedHands = true;
        }
        pillager.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        PTPackets.sendToAllPlayers(new PillagerPoseSyncPacket(true, pillager));
        //        mob.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof PillagerTradeGoal)
    }



    @Override
    public void stop() {

        // Hands need to be exchanged back in case the pillager had something in offhand
        // (iirc they sometimes spawn with rockets)
        // 1. pillager naturally spawn with barrier
        // 2. player get close to pillager from 1
        // 3. player initiates trade from 2
        // 4. player logs off (when joining the state is the same due to capability wow)
        // 5. player leaves area
        // changedHands        false    true     true    true    false
        // pillager inventory  barrier  empty    bush  | bush    barrier
        // in capability       empty    barrier  barrier barrier empty

        PTPackets.sendToAllPlayers(new PillagerPoseSyncPacket(false, pillager));
        PillagerTradeCapability cooldown = PillagerUtils.getCapability(pillager);
        if(cooldown.changedHands) {
            pillager.setItemInHand(InteractionHand.OFF_HAND, cooldown.initialOffHand);
            cooldown.initialOffHand = ItemStack.EMPTY;
            cooldown.changedHands = false;
        }
    }

}
