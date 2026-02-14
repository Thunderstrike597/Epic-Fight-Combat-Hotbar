package net.kenji.epic_fight_combat_hotbar.client;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.network.CombatHotbarPacketHandler;
import net.kenji.epic_fight_combat_hotbar.network.HotbarSelectSlotPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HotbarSlotHandler {
    public static Map<UUID, Integer> selectedSlotMap = new HashMap<>();

    public static int getSelectedSlot(Player player) {
        return selectedSlotMap.getOrDefault(player.getUUID(), 0);
    }
    
    public static void setSelectedSlot(Player player, int slot) {
        selectedSlotMap.put(player.getUUID(), Math.max(0, Math.min(3, slot))); // Clamp between 0-3
    }
    @OnlyIn(Dist.CLIENT)
    public static void setSelectedSlotSynchronised(Player player, int slot) {
        int newSlot = Math.max(0, Math.min(3, slot));
        selectedSlotMap.put(player.getUUID(), newSlot); // Clamp between 0-3
        CombatHotbarPacketHandler.sendToServer(new HotbarSelectSlotPacket(newSlot));
    }
}