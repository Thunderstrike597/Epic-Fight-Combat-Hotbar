package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    // Hook into when ANY container adds player inventory
    @Inject(
        method = "addSlot",
        at = @At("TAIL")
    )
    private void onSlotAdded(Slot pSlot, CallbackInfoReturnable<Slot> cir) {
        AbstractContainerMenu menu = (AbstractContainerMenu)(Object)this;

        if(pSlot.index < pSlot.container.getContainerSize() - 1) return;
        if(menu instanceof InventoryMenu) return;
        if (pSlot.container instanceof Inventory inventory) {
            if (!epicFight_CombatHotbar_Versions$hasCombatSlots(menu)) {
                    epicFight_CombatHotbar_Versions$addCombatHotbarSlots(menu, inventory.player);

            }
        }
    }
    
    @Unique
    private boolean epicFight_CombatHotbar_Versions$hasCombatSlots(AbstractContainerMenu menu) {
        // Check if any slot is a combat hotbar slot
        return menu.slots.stream().anyMatch(s -> s instanceof SlotItemHandler);
    }
    
    @Unique
    private void epicFight_CombatHotbar_Versions$addCombatHotbarSlots(AbstractContainerMenu menu, Player player) {
        AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker)(Object)this;

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            int startX = -18;

            Slot firstPlayerSlot = null;
            for (Slot slot : menu.slots) {
                if (slot.container instanceof Inventory) {
                    firstPlayerSlot = slot;
                    break;
                }
            }
            if(firstPlayerSlot == null)return;
            int startY = firstPlayerSlot.y + 1;
            
            for (int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
                invoker.epic_fight_combat_hotbar$addSlot(new SlotItemHandler(handler, i, startX, startY + (i * 18)));
            }
        });
    }

}