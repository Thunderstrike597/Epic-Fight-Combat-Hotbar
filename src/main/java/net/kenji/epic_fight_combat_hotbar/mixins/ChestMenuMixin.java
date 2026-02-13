package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestMenu.class)
public class ChestMenuMixin {
    
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method = "<init>*",
            at = @At("TAIL"))
    private void addWeaponSlots(MenuType<?> type, int id, Inventory playerInv, Container container, int rows, CallbackInfo ci) {
        playerInv.player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker)(Object)this;

            int startX = -19;
            int startY = 21;

            for (int i = 0; i < handler.getSlots(); i++) {
                invoker.epic_fight_combat_hotbar$addSlot(
                        new SlotItemHandler(handler, i, startX, startY + (i * 18))
                );
            }

            LOGGER.info("Combat hotbar slots added to ChestMenu: {} slots", handler.getSlots());
        });
    }
}