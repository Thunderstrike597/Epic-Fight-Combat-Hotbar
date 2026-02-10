package net.kenji.epic_fight_combat_hotbar.mixins;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper")
public abstract class SlotWrapperMixin extends Slot {
    @Shadow
    @Final
    public Slot target;

    protected SlotWrapperMixin(
            Container menu,
            int index,
            int x,
            int y
    ) {
        super(menu, index, x, y);
    }
}
