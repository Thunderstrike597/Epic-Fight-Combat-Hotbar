package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.api.CombatHotbarHandler;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {
   @Inject(method = "<init>", at = @At("TAIL"))
    private void addWeaponSlots(Inventory inv, boolean active, Player owner, CallbackInfo ci) {
       CombatHotbarHandler handler = CombatHotbarProvider.getCombatHotbarCap(owner);

       AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker) (Object) this;
       InventoryMenu menu = (InventoryMenu) (Object) this;

       int startX = -18;
       int startY = 28;

       // Get the current slot count BEFORE adding new slots
       int startIndex = menu.slots.size();

       for (int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
           invoker.epic_fight_combat_hotbar$addSlot(
                   new SlotItemHandler(handler, i, startX, startY + (i * 18))
           );
       }
   }
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void handleQuickMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        InventoryMenu menu = (InventoryMenu) (Object) this;
        Slot slot = menu.slots.get(slotIndex);

        if (slot == null || !slot.hasItem()) {
            return;
        }

        ItemStack stackToMove = slot.getItem();


        CombatHotbarHandler handler = CombatHotbarProvider.getCombatHotbarCap(player);
        int combatHotbarStartIndex = -1;
        if (!handler.isItemValid(0, stackToMove)) {
            return;
        }
        // Find where combat hotbar slots start in the menu
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot s = menu.slots.get(i);
            if (s instanceof SlotItemHandler && ((SlotItemHandler) s).getItemHandler() == handler) {
                combatHotbarStartIndex = i;
                break;
            }
        }

        if (combatHotbarStartIndex == -1) return;
        AbstractContainerMenuInvoker menuInvoker = (AbstractContainerMenuInvoker) menu;

        // Check if we're shift-clicking FROM a combat hotbar slot
        if (slotIndex >= combatHotbarStartIndex && slotIndex < combatHotbarStartIndex + handler.getSlots()) {
            // Move FROM combat hotbar TO main inventory
            // Try hotbar first (slots 36-44 in InventoryMenu), then main inventory (9-35)
            if (menuInvoker.moveItemStackToInvoker(stackToMove, 36, 45, false) ||
                    menuInvoker.moveItemStackToInvoker(stackToMove, 9, 36, false)) {

                slot.setChanged();
                if (stackToMove.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                }
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        } else {
            int combatHotbarEndIndex = combatHotbarStartIndex + handler.getSlots();

            if (menuInvoker.moveItemStackToInvoker(stackToMove, combatHotbarStartIndex, combatHotbarEndIndex, false)) {
                slot.setChanged();
                if (stackToMove.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                }
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        }
    }
}