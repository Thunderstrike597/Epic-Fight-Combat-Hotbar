package net.kenji.epic_fight_combat_hotbar.mixins.compat.corpse;

import de.maxhenkel.corpse.corelib.inventory.ItemListInventory;
import de.maxhenkel.corpse.gui.CorpseInventoryContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CorpseInventoryContainer.class)
public interface CorpseInventoryContainerInvoker {

    @Invoker("fill")
    void fillInvoker(List<ItemStack> additionalItems, Container inventory, NonNullList<ItemStack> playerInv);

    @Accessor("mainInventory")
    ItemListInventory getMainCorpseInventory();
    @Accessor("armorInventory")
    ItemListInventory getCorpseArmorInventory();
    @Accessor("offHandInventory")
    ItemListInventory getCorpseOffHandInventory();

}
