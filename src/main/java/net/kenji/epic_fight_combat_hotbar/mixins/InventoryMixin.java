package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.items.SlotItemHandler;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public class InventoryMixin {
   @Inject(method = "<init>", at = @At("TAIL"))
    private void addWeaponSlots(Inventory inv, boolean active, Player owner, CallbackInfo ci) {
        owner.getCapability(ModCapabilities.COMBAT_HOTBAR)
                .ifPresent(handler -> {
                    AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker)(Object)this;
                    InventoryMenu menu = (InventoryMenu)(Object)this;

                    int startX = -18;
                    int startY = 28;

                    // Get the current slot count BEFORE adding new slots
                    int startIndex = menu.slots.size();

                    for (int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
                        invoker.epic_fight_combat_hotbar$addSlot(
                                new SlotItemHandler(handler, i, startX, startY + (i * 18))
                        );
                    }

                    // Debug log to verify correct indices
                });
    }
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void handleQuickMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        InventoryMenu menu = (InventoryMenu)(Object)this;
        Slot slot = menu.slots.get(slotIndex);

        if (slot == null || !slot.hasItem()) {
            return;
        }

        ItemStack stackToMove = slot.getItem();

        // Only handle weapons
        if (!(stackToMove.getItem() instanceof SwordItem) &&
                !(stackToMove.getItem() instanceof ProjectileWeaponItem)) {
            return;
        }

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            // Try to move to combat hotbar
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack combatSlot = handler.getStackInSlot(i);

                if (combatSlot.isEmpty()) {
                    handler.setStackInSlot(i, stackToMove.copy());
                    slot.set(ItemStack.EMPTY);
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }
        });
    }
}