package net.kenji.epic_fight_combat_hotbar.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jline.utils.Log;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HotbarRenderHandler {
    
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static int selectedSlot = 0; // Track which combat slot is selected

    // Cancel normal hotbar rendering when in battle mode
    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            if (CombatModeHandler.isInBattleMode()) {
                event.setCanceled(true);

                Minecraft mc = Minecraft.getInstance();
                Player player = mc.player;
                if (player == null) return;
                player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
                    renderCombatHotbar(event.getGuiGraphics(), handler, mc);
                });
            }
        }
    }

    private static void renderCombatHotbar(GuiGraphics graphics, net.minecraftforge.items.ItemStackHandler handler, Minecraft mc) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int slots = 4;

        // Center the 4-slot hotbar
        // Vanilla centers 9 slots at screenWidth/2 - 91
        // 9 slots = 182 width, 4 slots = roughly 80 width
        int x = (screenWidth / 2) - 40; // Adjusted for 4 slots
        int y = screenHeight - 22;

        RenderSystem.enableBlend();

        // First pass: Draw all unselected slots
        for (int i = 0; i < slots; i++) {


            int slotX = x + (i * 20);
            int slotY = y;

            // Unselected slot: 23x22 at UV (0, 0)
            graphics.blit(WIDGETS_LOCATION, slotX, slotY, 1, 1, 20, 20);

            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (i == selectedSlot) continue;
                graphics.renderItem(stack, slotX + 3, slotY + 3);
                graphics.renderItemDecorations(mc.font, stack, slotX + 3, slotY + 3);
            }
        }

        // Second pass: Draw selected slot on top
        if (selectedSlot >= 0 && selectedSlot < slots) {
            int slotX = x + (selectedSlot * 20) - 1;
            int slotY = y - 1;

            // Selected slot: 24x24 at UV (0, 22) - note it's 1px larger on all sides
            graphics.blit(WIDGETS_LOCATION, slotX, slotY, 0, 22, 24, 24);

            ItemStack stack = handler.getStackInSlot(selectedSlot);
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, slotX + 4, slotY + 4);
                graphics.renderItemDecorations(mc.font, stack, slotX + 4, slotY + 4);
            }
        }

        RenderSystem.disableBlend();
    }
    public static int getSelectedSlot() {
        return selectedSlot;
    }
    
    public static void setSelectedSlot(int slot) {
        selectedSlot = Math.max(0, Math.min(3, slot)); // Clamp between 0-3
    }
}