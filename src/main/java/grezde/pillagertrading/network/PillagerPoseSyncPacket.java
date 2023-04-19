package grezde.pillagertrading.network;

import grezde.pillagertrading.client.PoseSyncUtil;
import grezde.pillagertrading.recipe.PillagerTradingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PillagerPoseSyncPacket {

    boolean add;
    UUID id;

    public PillagerPoseSyncPacket(boolean add, Pillager pillager) {
        this.add = add;
        this.id = pillager.getUUID();
    }

    public PillagerPoseSyncPacket(FriendlyByteBuf buf) {
        add = buf.readBoolean();
        id = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(add);
        buf.writeUUID(id);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // WE ARE ON CLIENT
            if(add)
                PoseSyncUtil.addToList(id);
            else
                PoseSyncUtil.removeFromList(id);
        });
        return true;
    }

}
