package grezde.pillagertrading.network;

import com.mojang.datafixers.util.Pair;
import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GetPillagerTradingRecipesPacket {

    public GetPillagerTradingRecipesPacket() {
    }

    public GetPillagerTradingRecipesPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            PTPackets.sendToPlayer(new SendPillagerTradingRecipesPacket(PillagerTradingRecipe.getAllRecipes(level)), player);
        });
        return true;
    }

}
