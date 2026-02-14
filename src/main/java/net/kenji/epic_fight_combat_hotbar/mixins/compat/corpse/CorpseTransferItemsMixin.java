package net.kenji.epic_fight_combat_hotbar.mixins.compat.corpse;

import de.maxhenkel.corpse.gui.CorpseInventoryContainer;
import de.maxhenkel.corpse.gui.Guis;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CorpseInventoryContainer.class, remap = false)
public class CorpseTransferItemsMixin {


    @Inject(method = "transferItems", at = @At("HEAD"), cancellable = true)
    public void addAdditionalItems(CallbackInfo ci){
        CorpseInventoryContainer container = (CorpseInventoryContainer)(Object)this;
        CorpseContainerBaseAccessor corpseContainerAccessor = (CorpseContainerBaseAccessor)container;
        CorpseInventoryContainerInvoker inventoryContainerInvoker = (CorpseInventoryContainerInvoker)container;

        ci.cancel();

        if (container.isEditable()) {
            if(corpseContainerAccessor.getInventory().player instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)corpseContainerAccessor.getInventory().player;
                NonNullList<ItemStack> additionalItems = NonNullList.create();
                NonNullList<ItemStack> combatHotbarItems = NonNullList.create();

                // EXTRACT combat hotbar items BEFORE fill() processes them
                extractCombatHotbarItems(inventoryContainerInvoker.getMainCorpseInventory(), combatHotbarItems);
                extractCombatHotbarItems(inventoryContainerInvoker.getCorpseArmorInventory(), combatHotbarItems);
                extractCombatHotbarItems(inventoryContainerInvoker.getCorpseOffHandInventory(), combatHotbarItems);

                // Now run fill() on the remaining items
                inventoryContainerInvoker.fillInvoker(additionalItems, inventoryContainerInvoker.getMainCorpseInventory(), corpseContainerAccessor.getInventory().items);
                inventoryContainerInvoker.fillInvoker(additionalItems, inventoryContainerInvoker.getCorpseArmorInventory(), corpseContainerAccessor.getInventory().armor);
                inventoryContainerInvoker.fillInvoker(additionalItems, inventoryContainerInvoker.getCorpseOffHandInventory(), corpseContainerAccessor.getInventory().offhand);

                additionalItems.addAll(container.getCorpse().getDeath().getAdditionalItems());

                // Check additionalItems for combat hotbar items too
                NonNullList<ItemStack> regularItems = NonNullList.create();
                for(ItemStack stack : additionalItems) {
                    if (stack.getTag() != null && stack.getTag().contains(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG)) {
                        combatHotbarItems.add(stack);
                    } else {
                        regularItems.add(stack);
                    }
                }

                NonNullList<ItemStack> restItems = NonNullList.create();

                // Process regular items
                for(ItemStack stack : regularItems) {
                    if (!player.getInventory().add(stack)) {
                        restItems.add(stack);
                    }
                }

                // Process combat hotbar items with BACKUP
                player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent((handler) -> {
                    for(ItemStack stack : combatHotbarItems) {
                        boolean placed = false;

                        if (stack.getTag() != null && stack.getTag().contains(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG)) {
                            int slot = stack.getTag().getInt(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG);
                            ItemStack existing = handler.getStackInSlot(slot);

                            if (existing.isEmpty()) {
                                handler.setStackInSlot(slot, stack);
                                placed = true;
                                System.out.println("Placed " + stack + " in combat hotbar slot " + slot);
                            }
                        }

                        // BACKUP: If not placed in combat hotbar, try regular inventory
                        if (!placed) {
                            System.out.println("Could not place in combat hotbar, trying regular inventory for " + stack);
                            if (!player.getInventory().add(stack)) {
                                restItems.add(stack);
                                System.out.println("Added to rest items: " + stack);
                            }
                        }
                    }
                });

                // FALLBACK: If capability didn't exist, add ALL combat items to regular inventory
                if (!player.getCapability(ModCapabilities.COMBAT_HOTBAR).isPresent()) {
                    System.out.println("Combat hotbar capability not present! Adding items to regular inventory");
                    for(ItemStack stack : combatHotbarItems) {
                        if (!player.getInventory().add(stack)) {
                            restItems.add(stack);
                        }
                    }
                }

                container.getCorpse().getDeath().getAdditionalItems().clear();
                container.getCorpse().getDeath().getAdditionalItems().addAll(restItems);

                if (!container.getCorpse().getDeath().getAdditionalItems().isEmpty()) {
                    Guis.openAdditionalItems(player, container);
                }
            }
        }
    }

    private void extractCombatHotbarItems(Container inventory, NonNullList<ItemStack> combatItems) {
        for(int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getTag() != null &&
                    stack.getTag().contains(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG)) {
                combatItems.add(stack.copy());
                inventory.setItem(i, ItemStack.EMPTY); // Remove from corpse inventory
            }
        }
    }
}
