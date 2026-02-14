package net.kenji.epic_fight_combat_hotbar.network;

import net.kenji.epic_fight_combat_hotbar.client.HotbarSlotHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class HotbarSelectSlotPacket {
    private final int slot;

    public HotbarSelectSlotPacket(int slot) {
        this.slot = slot;
    }

    // Encode: Write data to buffer
    public static void encode(HotbarSelectSlotPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
    }

    // Decode: Read data from buffer
    public static HotbarSelectSlotPacket decode(FriendlyByteBuf buf) {
        int slot = buf.readInt();
        return new HotbarSelectSlotPacket(slot);
    }

    // Handle: Process the packet on the receiving side
    public static void handle(HotbarSelectSlotPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // This runs on the server thread
            HotbarSlotHandler.setSelectedSlot(packet.slot);
        });
        ctx.get().setPacketHandled(true);
    }
}