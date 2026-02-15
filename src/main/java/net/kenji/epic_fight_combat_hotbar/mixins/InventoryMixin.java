package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraftforge.items.SlotItemHandler;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

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
        if (!(stackToMove.getItem() instanceof TieredItem) || stackToMove.getItem() instanceof Equipable || !(stackToMove.getItem() instanceof ProjectileWeaponItem)) {
            if(EpicFightCapabilities.getItemStackCapability(stackToMove) == null) {
                return;
            }
            CapabilityItem capItem = EpicFightCapabilities.getItemStackCapability(stackToMove);
            if(capItem != null) {
                if(capItem.getWeaponCategory() == CapabilityItem.WeaponCategories.NOT_WEAPON || capItem.getWeaponCategory() == CapabilityItem.WeaponCategories.PICKAXE || capItem.getWeaponCategory() == CapabilityItem.WeaponCategories.HOE || capItem.getWeaponCategory() == CapabilityItem.WeaponCategories.SHIELD){
                    return;
                }
            }
        }

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            int combatHotbarStartIndex = -1;

            // Find where combat hotbar slots start in the menu
            for (int i = 0; i < menu.slots.size(); i++) {
                Slot s = menu.slots.get(i);
                if (s instanceof SlotItemHandler && ((SlotItemHandler)s).getItemHandler() == handler) {
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
        });
    }
}