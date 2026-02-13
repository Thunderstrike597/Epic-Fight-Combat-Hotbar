package net.kenji.epic_fight_combat_hotbar.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.item.WeaponItem;

public class CombatHotbarProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final int SLOTS = 4;

    private final ItemStackHandler inventory = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() instanceof SwordItem ||
                    stack.getItem() instanceof ProjectileWeaponItem;
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