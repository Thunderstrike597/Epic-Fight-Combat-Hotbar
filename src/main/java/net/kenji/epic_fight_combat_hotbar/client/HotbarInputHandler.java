package net.kenji.epic_fight_combat_hotbar.client;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HotbarInputHandler {
    
    // Handle number key presses
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
       if(Minecraft.getInstance().player != null) {
           if (!CombatModeHandler.isInBattleMode(Minecraft.getInstance().player)) {
               return;
           }

           Minecraft mc = Minecraft.getInstance();
           if (mc.player == null || mc.screen != null) {
               return;
           }

           // Check if keys 1-4 are pressed
           if (event.getAction() == GLFW.GLFW_PRESS) {
               int key = event.getKey();

               if (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_4) {
                   int slot = key - GLFW.GLFW_KEY_1; // Convert to 0-3
                   HotbarRenderHandler.setSelectedSlot(slot);


               }
           }
       }
    }
    
    // Handle mouse scroll
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().player != null) {
            if (!CombatModeHandler.isInBattleMode(Minecraft.getInstance().player)) {
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) {
                return;
            }

            double scrollDelta = event.getScrollDelta();
            int currentSlot = HotbarRenderHandler.getSelectedSlot();

            if (scrollDelta > 0) {
                // Scroll up - previous slot
                currentSlot--;
                if (currentSlot < 0) currentSlot = 3;
            } else if (scrollDelta < 0) {
                // Scroll down - next slot
                currentSlot++;
                if (currentSlot > 3) currentSlot = 0;
            }

            HotbarRenderHandler.setSelectedSlot(currentSlot);

            // Cancel the event so vanilla hotbar doesn't scroll
            event.setCanceled(true);

            // Sync to server
            // NetworkHandler.sendToServer(new SetCombatSlotPacket(currentSlot));
        }
    }
}