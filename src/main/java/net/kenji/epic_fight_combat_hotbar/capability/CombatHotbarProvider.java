package net.kenji.epic_fight_combat_hotbar.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.item.WeaponItem;

public class CombatHotbarProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final int SLOTS = 4;
    public static final String COMBAT_HOTBAR_SLOT_ITEM_TAG = "combat_hotbar_slot_item";

    private final ItemStackHandler inventory = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            CapabilityItem capItem = EpicFightCapabilities.getItemStackCapability(stack);
            if(capItem != null){
                return capItem.getWeaponCategory() != CapabilityItem.WeaponCategories.NOT_WEAPON && capItem.getWeaponCategory() != CapabilityItem.WeaponCategories.PICKAXE && capItem.getWeaponCategory() != CapabilityItem.WeaponCategories.HOE;
            }
            return false;
        }
        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            super.setStackInSlot(slot, stack);
            if(stack.getTag() != null) {
                stack.getTag().putInt(COMBAT_HOTBAR_SLOT_ITEM_TAG, slot);
            }
        }
    };

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> inventory);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.COMBAT_HOTBAR) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();
    }
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt);
    }

    public void invalidate() {
        optional.invalidate();
    }
}