package net.kenji.epic_fight_combat_hotbar.api;

import net.kenji.epic_fight_combat_hotbar.client.HotbarSlotHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class CombatHotbarHandler extends ItemStackHandler {
    private static ItemStack originalMainHandStack = ItemStack.EMPTY;

    protected CombatHotbarHandler(int SLOTS){
        super(SLOTS);
    }
    public void setOriginalMainHandStack(ItemStack stack){
        originalMainHandStack = stack;
    }
    public ItemStack getOriginalMainHandItem(Player player){
        ItemStack finalStack = originalMainHandStack;
        if(finalStack == null || finalStack == ItemStack.EMPTY) {
            finalStack = this.getStackInSlot(HotbarSlotHandler.getSelectedSlot(player));
        }
        return finalStack;
    }
}
