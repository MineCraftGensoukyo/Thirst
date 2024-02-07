package moe.gensoukyo.thirst.net;

import moe.gensoukyo.thirst.net.pkg.CisternUseLocPack;
import moe.gensoukyo.thirst.net.pkg.CisternUseResPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static moe.gensoukyo.thirst.Thirst.MODID;

public class Channel {
    public static SimpleChannel INSTANCE;
    public static String VERSION = "1.0";

    private static int id = 0;

    public static int nextId() {
        return id++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MODID, "water_channel"))
                .networkProtocolVersion(() -> VERSION)
                .clientAcceptedVersions(VERSION::equals)
                .serverAcceptedVersions(VERSION::equals)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(CisternUseLocPack.class, nextId())
                .encoder(CisternUseLocPack::toBytes)
                .decoder(CisternUseLocPack::new)
                .consumerMainThread(CisternUseLocPack::handle)
                .add();

        net.messageBuilder(CisternUseResPack.class, nextId())
                .encoder(CisternUseResPack::toBytes)
                .decoder(CisternUseResPack::new)
                .consumerMainThread(CisternUseResPack::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
