package grezde.pillagertrading.network;

import grezde.pillagertrading.PTMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PTPackets {

    private static SimpleChannel CHANNEL;
    private static String VERSION = "0.2";

    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(PTMod.MODID, "messages"))
                .networkProtocolVersion(() -> VERSION)
                .clientAcceptedVersions(s -> s.equals(VERSION))
                .serverAcceptedVersions(s -> s.equals(VERSION))
                .simpleChannel();

        // to register
        //   net.messageBuilder(ExampleServerboundPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).decoder(ExampleServerboundPacket::new).encoder(ExampleServerboundPacket::toBytes).consumerMainThread(ExampleServerboundPacket::handle);
        // to write
        //   PTPackets.sendToServer(new ExampleServerboundPacket())

        net.messageBuilder(GetPillagerTradingRecipesPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).decoder(GetPillagerTradingRecipesPacket::new).encoder(GetPillagerTradingRecipesPacket::toBytes).consumerMainThread(GetPillagerTradingRecipesPacket::handle).add();
        net.messageBuilder(SendPillagerTradingRecipesPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).decoder(SendPillagerTradingRecipesPacket::new).encoder(SendPillagerTradingRecipesPacket::toBytes).consumerMainThread(SendPillagerTradingRecipesPacket::handle).add();
        net.messageBuilder(LockManuscriptPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).decoder(LockManuscriptPacket::new).encoder(LockManuscriptPacket::toBytes).consumerMainThread(LockManuscriptPacket::handle).add();
        net.messageBuilder(PillagerPoseSyncPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).decoder(PillagerPoseSyncPacket::new).encoder(PillagerPoseSyncPacket::toBytes).consumerMainThread(PillagerPoseSyncPacket::handle).add();


        CHANNEL = net;
    }

    public static <MSG> void sendToServer(MSG message) {
        CHANNEL.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

}
