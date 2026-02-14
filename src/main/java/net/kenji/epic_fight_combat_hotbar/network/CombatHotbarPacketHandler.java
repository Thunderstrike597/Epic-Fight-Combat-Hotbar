package net.kenji.epic_fight_combat_hotbar.network;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class CombatHotbarPacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(EpicFightCombatHotbar.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.messageBuilder(HotbarSelectSlotPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(HotbarSelectSlotPacket::decode)
                .encoder(HotbarSelectSlotPacket::encode)
                .consumerMainThread(HotbarSelectSlotPacket::handle)
                .add();

    }

    // Helper method to send packet to server
    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }


    // Helper method to send packet to specific player
    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    // Helper method to send packet to all players
    public static void sendToAll(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }
}