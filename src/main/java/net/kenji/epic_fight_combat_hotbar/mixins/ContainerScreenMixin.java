package net.kenji.epic_fight_combat_hotbar.mixins;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.client.gui.screens.inventory.ContainerScreen.class)
public class ContainerScreenMixin {
    @Unique
    private static final ResourceLocation COMBAT_HOTBAR_SLOTS =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/inventory.png");
    @Inject(method = "renderBg", at = @At("TAIL"))
    private void addWeaponSlots(GuiGraphics pGuiGraphics, float par2, int par3, int par4, CallbackInfo ci) {

        AbstractContainerScreen screen = (AbstractContainerScreen) (Object) this;
        if(screen instanceof CreativeModeInventoryScreen) return;
        int leftPos = ((InventoryScreenAccessor) screen).getLeftPos();
        int topPos = ((InventoryScreenAccessor) screen).getTopPos();
        // These coordinates are RELATIVE to the screen's leftPos/topPos
        // They should match the slot positions: 180, 20
        int slotX = leftPos - 20;
        int slotY = topPos + 20;

        // Draw slot backgrounds
        for (int i = 0; i < 4; i++) {
            pGuiGraphics.blit(
                    COMBAT_HOTBAR_SLOTS,
                    slotX, slotY + (i * 18),
                    7, 7,
                    18, 18,
                    256, 256
            );
        }
    }
}
