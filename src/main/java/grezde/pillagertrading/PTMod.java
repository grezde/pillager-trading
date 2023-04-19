package grezde.pillagertrading;

import com.mojang.logging.LogUtils;
import grezde.pillagertrading.blocks.PTBlocks;
import grezde.pillagertrading.entity.PillagerEvents;
import grezde.pillagertrading.entity.PillagerTradeGoal;
import grezde.pillagertrading.items.PTItems;
import grezde.pillagertrading.loot.PTLootModifiers;
import grezde.pillagertrading.network.PTPackets;
import grezde.pillagertrading.recipe.PTRecipes;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import grezde.pillagertrading.util.PTSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PTMod.MODID)
public class PTMod
{
    public static final String MODID = "pillagertrading";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PTMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        PTItems.register(modEventBus);
        PTBlocks.register(modEventBus);
        PTRecipes.register(modEventBus);
        PTSounds.register(modEventBus);;

        modEventBus.register(PillagerEvents.ModBusEvents.class);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PillagerEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            PTPackets.register();
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // TODO
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
