package grezde.pillagertrading.network;

import grezde.pillagertrading.client.gui.IllagerManuscriptScreen;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SendPillagerTradingRecipesPacket {

    List<PillagerTradingRecipe> recipes;

    public SendPillagerTradingRecipesPacket(List<PillagerTradingRecipe> recipes) {
        this.recipes = recipes;
    }

    public SendPillagerTradingRecipesPacket(FriendlyByteBuf buf) {
        int len = buf.readInt();
        recipes = new ArrayList<>();
        for(int i=0; i<len; i++) {
            ResourceLocation key = buf.readResourceLocation();
            PillagerTradingRecipe ptr = PillagerTradingRecipe.Serializer.INSTANCE.fromNetwork(key, buf);
            recipes.add(ptr);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(recipes.size());
        for(int i=0; i < recipes.size(); i++) {
            buf.writeResourceLocation(recipes.get(i).getId());
            PillagerTradingRecipe.Serializer.INSTANCE.toNetwork(buf, recipes.get(i));
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            IllagerManuscriptScreen.updateRecipes(recipes);
        });
        return true;
    }

}
