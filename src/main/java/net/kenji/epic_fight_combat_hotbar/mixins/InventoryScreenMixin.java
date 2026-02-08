package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    private static final ResourceLocation COMBAT_HOTBAR_SLOTS =
            new ResourceLocation("minecraft", "textures/gui/container/inventory.png");

    @Inject(method = "renderBg", at = @At("RETURN"))
    protected void renderCombatHotbarSlots(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        int leftPos = ((InventoryScreenAccessor) screen).getLeftPos();
        int topPos = ((InventoryScreenAccessor) screen).getTopPos();
        // These coordinates are RELATIVE to the screen's leftPos/topPos
        // They should match the slot positions: 180, 20
        int slotX = leftPos - 20;
        int slotY = topPos + 20;

        // Draw slot backgrounds
        for (int i = 0; i < 4; i++) {
            guiGraphics.blit(
                    COMBAT_HOTBAR_SLOTS,
                    slotX, slotY + (i * 18),
                    7, 7,
                    18, 18,
                    256, 256
            );
        }
    }
}