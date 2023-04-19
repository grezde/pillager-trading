package grezde.pillagertrading.entity;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.entity.capability.PillagerTradeCapability;
import grezde.pillagertrading.entity.capability.PillagerTradeCapabilityProvider;
import grezde.pillagertrading.network.PTPackets;
import grezde.pillagertrading.network.PillagerPoseSyncPacket;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import grezde.pillagertrading.util.PTSounds;
import grezde.pillagertrading.util.PillagerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

public class PillagerEvents {

    @SubscribeEvent
    public static void onPillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getSide() != LogicalSide.SERVER && !(event.getTarget() instanceof Pillager))
            return;
        Player player = event.getEntity();
        Pillager pillager = (Pillager)event.getTarget();
        PillagerTradeGoal goal = PillagerUtils.getGoal(pillager);
        PillagerTradeCapability cooldown = PillagerUtils.getCapability(pillager);

        if(!goal.canTrade || goal.tradePlayer != player) {
            if(cooldown.canPlaySound()) {
                pillager.playSound(PTSounds.PILLAGER_DECLINE.get());
                cooldown.resetSound();
            }
            return;
        }
        pillager.playSound(PTSounds.PILLAGER_TRADE.get());
        cooldown.resetSound();

        ItemStack theItem = PillagerUtils.getPrice(player);
        PillagerTradingRecipe recipe = PillagerUtils.getRecipe(player);
        goal.canTrade = false;
        PillagerUtils.getCapability(pillager).reset(1);
        PillagerUtils.playerGive(player, recipe.getResultItem());
        theItem.setCount(theItem.getCount() - recipe.getInput().getCount());
        player.containerMenu.broadcastChanges();
    }

    @SubscribeEvent
    public static void onPillagerAppear(EntityJoinLevelEvent event) {
        if(event.getLevel() instanceof ServerLevel && event.getEntity() instanceof Pillager pillager
                && PillagerUtils.shouldHaveTradeAI(pillager)
                && !PillagerUtils.hasTradeAI(pillager)) {

                try {
                    pillager.goalSelector.addGoal(1, new PillagerTradeGoal(pillager));
                } catch(IllegalArgumentException e) {
                    // This might happen in this mod too, but this section is taken from Quark "Villagers follow Emeralds"
                    // This appears to be a weird bug that happens when a villager is riding something and its chunk unloads
                }
        }
    }

    @SubscribeEvent
    public static void onPillagerHurt(LivingHurtEvent event) {
        if(event.getEntity() instanceof Pillager pillager && event.getSource().getEntity() instanceof Player player) {
            if(PillagerUtils.hasTradeAI(pillager)) {
                PillagerUtils.getCapability(pillager).reset(2);
                PillagerUtils.destroyTradeAI(pillager);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCapability(RegisterCapabilitiesEvent event) {
        event.register(PillagerTradeCapability.class);
    }

    @SubscribeEvent
    public static void onPillagerAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Pillager pillager) {
            if(!pillager.getCapability(PillagerTradeCapabilityProvider.PILLAGER_TRADE_COOLDOWN).isPresent())
                event.addCapability(new ResourceLocation(PTMod.MODID, "properties"), new PillagerTradeCapabilityProvider());
        }
    }

    @Mod.EventBusSubscriber(modid = PTMod.MODID)
    public class ModBusEvents {



    }

}
