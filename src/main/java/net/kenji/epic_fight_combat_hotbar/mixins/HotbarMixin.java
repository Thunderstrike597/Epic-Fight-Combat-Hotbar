package net.kenji.epic_fight_combat_hotbar.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.kenji.epic_fight_combat_hotbar.client.HotbarRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HotbarMixin {

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbarCombatHotbar(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci){
        Player player = Minecraft.getInstance().player;

        if(player == null) return;
        if(!CombatModeHandler.isInBattleMode()) return;
        ci.cancel();
        Gui gui = (Gui)(Object)this;

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            epic_fight_combat_hotbar$renderCombatHotbar(pGuiGraphics, handler, Minecraft.getInstance(), gui);
        });
    }

    @Unique
    private static void epic_fight_combat_hotbar$renderCombatHotbar(GuiGraphics graphics, net.minecraftforge.items.ItemStackHandler handler, Minecraft mc, Gui gui) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int selectedSlot = HotbarRenderHandler.getSelectedSlot();
        ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

        int slots = 4;
        GuiAccessor guiInvoker = (GuiAccessor) gui;
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
                guiInvoker.invokeRenderSlot(graphics, slotX + 3, slotY + 3, Minecraft.getInstance().getPartialTick(), player, stack, i);
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
                guiInvoker.invokeRenderSlot(graphics, slotX + 4, slotY + 4, Minecraft.getInstance().getPartialTick(), player, stack, selectedSlot);
            }
        }

        RenderSystem.disableBlend();
    }
}
