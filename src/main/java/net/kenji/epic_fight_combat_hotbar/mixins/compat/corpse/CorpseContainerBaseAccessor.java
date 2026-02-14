package net.kenji.epic_fight_combat_hotbar.mixins.compat.corpse;

import de.maxhenkel.corpse.entities.CorpseEntity;
import de.maxhenkel.corpse.gui.CorpseContainerBase;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CorpseContainerBase.class)

public interface CorpseContainerBaseAccessor {

    @Accessor("playerInventory")
    Inventory getInventory();
}
