package grezde.pillagertrading.client;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.client.gui.IllagerManuscriptScreen;
import grezde.pillagertrading.items.IllagerManuscriptItem;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PTMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        IllagerManuscriptItem.onRightClick = (level, player, hand) -> {
            ItemStack itemstack = player.getItemInHand(hand);
            player.playSound(SoundEvents.BOOK_PAGE_TURN);
            Minecraft.getInstance().setScreen(new IllagerManuscriptScreen(player, itemstack, hand));
        };
    }
}