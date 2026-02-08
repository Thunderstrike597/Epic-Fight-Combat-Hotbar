package net.kenji.epic_fight_combat_hotbar.containers;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WeaponSlotContainer implements Container {
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return stack.split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack old = stack;
        stack = ItemStack.EMPTY;
        return old;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        stack = ItemStack.EMPTY;
    }
}