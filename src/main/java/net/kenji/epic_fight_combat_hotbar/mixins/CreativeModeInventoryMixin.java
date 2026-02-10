package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCreativeWeaponSlots(CallbackInfo ci) {
        addCustomSlots();
    }

    // This method is called when switching tabs
    @Inject(method = "selectTab", at = @At("TAIL"))
    private void onTabSwitch(CreativeModeTab tab, CallbackInfo ci) {
        if(tab.getType() == CreativeModeTab.Type.INVENTORY)
            addCustomSlots();
    }

    private void addCustomSlots() {
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen)(Object)this;
        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        AbstractContainerMenu menu = screen.getMenu();
        if (menu == null) return;

        // First, remove any existing custom slots to avoid duplicates
        menu.slots.removeIf(slot -> slot instanceof SlotItemHandler);

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            int startX = -19;
            int startY = 21;

            for (int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
                SlotItemHandler slot = new SlotItemHandler(handler, i, startX, startY + (i * 18));

                ((AbstractContainerMenuInvoker) menu)
                        .epic_fight_combat_hotbar$addSlot(slot);            }
        });
    }
}