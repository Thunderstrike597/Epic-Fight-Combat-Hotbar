package net.kenji.epic_fight_combat_hotbar.capability.handlers;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.world.item.WeaponItem;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID)
public class ItemPickupHandler {
    
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack pickedUpStack = event.getItem().getItem();
        
        // Only handle weapons (customize this logic)
        if (!(pickedUpStack.getItem() instanceof WeaponItem) && 
            !(pickedUpStack.getItem() instanceof net.minecraft.world.item.ProjectileWeaponItem)) {
            return;
        }
        
        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            // Try to insert into combat hotbar first
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack slotStack = handler.getStackInSlot(i);
                
                if (slotStack.isEmpty()) {
                    // Empty slot - insert here
                    handler.setStackInSlot(i, pickedUpStack.copy());
                    event.getItem().getItem().shrink(pickedUpStack.getCount());
                    event.setCanceled(true); // Prevent normal pickup
                    return;
                } else if (ItemStack.isSameItemSameTags(slotStack, pickedUpStack)) {
                    // Stackable - try to merge
                    int maxStack = Math.min(handler.getSlotLimit(i), slotStack.getMaxStackSize());
                    int spaceLeft = maxStack - slotStack.getCount();
                    
                    if (spaceLeft > 0) {
                        int amountToAdd = Math.min(spaceLeft, pickedUpStack.getCount());
                        slotStack.grow(amountToAdd);
                        pickedUpStack.shrink(amountToAdd);
                        
                        if (pickedUpStack.isEmpty()) {
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        });
    }
}