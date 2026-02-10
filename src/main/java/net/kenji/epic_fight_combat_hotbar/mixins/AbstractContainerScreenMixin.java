package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(
        method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelCombatHotbarTooltip(
            GuiGraphics pGuiGraphics, int pX, int pY, CallbackInfo ci
    ) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (!player.isCreative()) return;
        if (!(mc.screen instanceof AbstractContainerScreen<?>)) return;

        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>)(Object)this;

        Slot hovered = self.getSlotUnderMouse();
        if (hovered == null) return;

        ItemStack stack = hovered.getItem();
        if (stack.isEmpty()) return;

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            for (int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
                ItemStack hotbarStack = handler.getStackInSlot(i);
                if (!hotbarStack.isEmpty()
                        && ItemStack.isSameItemSameTags(stack, hotbarStack)) {
                    ci.cancel();
                }
            }
        });
    }
}