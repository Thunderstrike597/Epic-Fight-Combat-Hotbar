package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
    protected void renderCombatHotbarSlots(GuiGraphics pGuiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        InventoryScreen screen = (InventoryScreen) (Object) this;
        int leftPos = ((InventoryScreenAccessor) screen).getLeftPos();
        int topPos = ((InventoryScreenAccessor) screen).getTopPos();

        // Match the slot positioning exactly
        int startX = -19;
        int startY = 27;

        // Draw slot backgrounds
        for (int i = 0; i < 4; i++) {
            pGuiGraphics.blit(
                    COMBAT_HOTBAR_SLOTS,
                    leftPos + startX,           // X position matches slot
                    topPos + startY + (i * 18), // Y position matches slot
                    7, 7,
                    18, 18,
                    256, 256
            );
        }
    }
}