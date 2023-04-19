package grezde.pillagertrading.network;

import grezde.pillagertrading.items.PTItems;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.network.NetworkEvent;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class LockManuscriptPacket {

    boolean mainHand;
    ResourceLocation rl;

    public LockManuscriptPacket(InteractionHand hand, ResourceLocation recipeLocation) {
        this.mainHand = hand == InteractionHand.MAIN_HAND;
        this.rl = recipeLocation;
    }

    public LockManuscriptPacket(FriendlyByteBuf buf) {
        mainHand = buf.readBoolean();
        rl = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(mainHand);
        buf.writeResourceLocation(rl);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            Optional<? extends Recipe<?>> maybeRecipe = level.getRecipeManager().byKey(rl);
            if(maybeRecipe.isPresent() && (maybeRecipe.get() instanceof PillagerTradingRecipe ptr)) {
                ItemStack theOrder = new ItemStack(PTItems.ILLAGER_ORDER.get());
                CompoundTag tag = new CompoundTag();
                tag.putString("recipe", ptr.getId().toString());
                theOrder.setTag(tag);
                player.setItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, theOrder);
            }
        });
        return true;
    }

}