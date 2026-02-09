package net.kenji.epic_fight_combat_hotbar.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
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
        int x = (screenWidth / 2) - 40; // Adjusted for 4 slots
        int y = screenHeight - 22;

        Player player = mc.player;
        if (player == null) return;

        RenderSystem.enableBlend();

        // First pass: Draw all unselected slots
        for (int i = 0; i < slots; i++) {
            int slotX = x + (i * 22);
            int slotY = y;

            // Unselected slot: 20x20 at UV (1, 1)
            graphics.blit(WIDGETS_LOCATION, slotX, slotY, 24, 23, 22, 22);

            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && i != selectedSlot) {
                renderSlot(graphics, slotX + 3, slotY + 3, Minecraft.getInstance().getPartialTick(), player, stack, i);
            }
        }

        // Second pass: Draw selected slot on top
        if (selectedSlot >= 0 && selectedSlot < slots) {
            int slotX = x + (selectedSlot * 22) - 1;
            int slotY = y - 1;

            // Selected slot: 24x24 at UV (0, 22) - note it's 1px larger on all sides
            graphics.blit(WIDGETS_LOCATION, slotX, slotY, 0, 22, 24, 24);

            ItemStack stack = handler.getStackInSlot(selectedSlot);
            if (!stack.isEmpty()) {
                renderSlot(graphics, slotX + 4, slotY + 4, Minecraft.getInstance().getPartialTick(), player, stack, selectedSlot);
            }
        }

        RenderSystem.disableBlend();
    }

    private static void renderSlot(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed) {
        if (!pStack.isEmpty()) {
            float f = (float)pStack.getPopTime() - pPartialTick;
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate((float)(pX + 8), (float)(pY + 12), 0.0F);
                pGuiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                pGuiGraphics.pose().translate((float)(-(pX + 8)), (float)(-(pY + 12)), 0.0F);
            }

            pGuiGraphics.renderItem(pPlayer, pStack, pX, pY, pSeed);
            if (f > 0.0F) {
                pGuiGraphics.pose().popPose();
            }

            pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, pStack, pX, pY);
        }
    }
    public static int getSelectedSlot() {
        return selectedSlot;
    }
    
    public static void setSelectedSlot(int slot) {
        selectedSlot = Math.max(0, Math.min(3, slot)); // Clamp between 0-3
    }
}