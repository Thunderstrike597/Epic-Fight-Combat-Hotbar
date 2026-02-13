package net.kenji.epic_fight_combat_hotbar.mixins;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface InventoryScreenAccessor {


    @Accessor("leftPos")
    int getLeftPos();
    
    @Accessor("topPos")
    int getTopPos();

}