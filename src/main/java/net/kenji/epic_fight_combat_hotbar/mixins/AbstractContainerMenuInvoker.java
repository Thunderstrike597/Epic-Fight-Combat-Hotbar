package net.kenji.epic_fight_combat_hotbar.mixins;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuInvoker {

    @Invoker("addSlot")
    Slot epic_fight_combat_hotbar$addSlot(Slot slot);
    @Invoker("moveItemStackTo")
    boolean moveItemStackToInvoker(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection);

}