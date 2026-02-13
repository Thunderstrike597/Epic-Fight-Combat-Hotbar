package net.kenji.epic_fight_combat_hotbar.client;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HotbarRenderHandler {
    
    private static int selectedSlot = 0; // Track which combat slot is selected

    public static int getSelectedSlot() {
        return selectedSlot;
    }
    
    public static void setSelectedSlot(int slot) {
        selectedSlot = Math.max(0, Math.min(3, slot)); // Clamp between 0-3
    }
}