package grezde.pillagertrading.entity;

import com.mojang.logging.LogUtils;
import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.blocks.WardingStatueBlock;
import grezde.pillagertrading.blocks.WardingStatueBlockEntity;
import grezde.pillagertrading.entity.capability.PillagerTradeCapability;
import grezde.pillagertrading.entity.capability.PillagerTradeCapabilityProvider;
import grezde.pillagertrading.network.PTPackets;
import grezde.pillagertrading.network.PillagerPoseSyncPacket;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import grezde.pillagertrading.util.MiscUtils;
import grezde.pillagertrading.util.PTConfig;
import grezde.pillagertrading.util.PTSounds;
import grezde.pillagertrading.util.PillagerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.stringtemplate.v4.misc.Misc;

import java.util.function.Predicate;
import java.util.logging.Level;

public class PillagerEvents {

    @SubscribeEvent
    public static void onPillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getSide() != LogicalSide.SERVER && !(event.getTarget() instanceof Pillager))
            return;
        Player player = event.getEntity();
        Pillager pillager = (Pillager)event.getTarget();
        if(!(player instanceof ServerPlayer))
            return;
        try {
            PillagerTradeGoal goal = PillagerUtils.getGoal(pillager);
            PillagerTradeCapability cooldown = PillagerUtils.getCapability(pillager);

            if (!goal.canTrade || goal.tradePlayer != player) {
                if (cooldown.canPlaySound()) {
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
            MiscUtils.playerGive(player, recipe.getResultItem());
            theItem.setCount(theItem.getCount() - recipe.getInput().getCount());
        }
        catch (Exception e) {
            LogUtils.getLogger().info("Big error boo");
        }
    }

    @SubscribeEvent
    public static void onEntityAppear(EntityJoinLevelEvent event) {
        if(event.getLevel() instanceof ServerLevel) {
            if(event.getEntity() instanceof Pillager pillager
                && PillagerUtils.shouldHaveTradeAI(pillager)
                && !PillagerUtils.hasTradeAI(pillager)) {
                    pillager.goalSelector.addGoal(1, new PillagerTradeGoal(pillager));
            }
            else if(event.getEntity() instanceof Villager villager
                && !MiscUtils.villagerHasAI(villager))
                MiscUtils.villagerAddAI(villager);
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

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        boolean wanderSpawn = event.getEntity() instanceof WanderingTrader && event.getSpawnReason() == MobSpawnType.EVENT;
        boolean patrolSpawn = event.getSpawnReason() == MobSpawnType.PATROL;
        if(wanderSpawn || patrolSpawn){
            Predicate<BlockEntity> condition = wanderSpawn ?
                    (be -> be instanceof WardingStatueBlockEntity we && we.getBlockState().getValue(WardingStatueBlock.LEFT_ARM) && we.getBlockState().getValue(WardingStatueBlock.ACTIVE)) :
                    (be -> be instanceof WardingStatueBlockEntity we && we.getBlockState().getValue(WardingStatueBlock.RIGHT_ARM) && we.getBlockState().getValue(WardingStatueBlock.ACTIVE));
            boolean shouldStop = MiscUtils.getChunksInRange(event.getLevel(), event.getEntity().getOnPos(), PTConfig.STATUE_RANGE.get())
                    .anyMatch(chunk -> MiscUtils.containsBlockEntity(chunk, condition));
            event.setCanceled(shouldStop);
        }
    }

    @Mod.EventBusSubscriber(modid = PTMod.MODID)
    public class ModBusEvents {



    }

}
