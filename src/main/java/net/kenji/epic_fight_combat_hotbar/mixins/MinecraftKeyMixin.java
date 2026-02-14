package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftKeyMixin {

    @Redirect(
            method = "handleKeybinds",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Inventory;selected:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void cancelHotbarSelection(Inventory inventory, int value) {
        Minecraft mc = (Minecraft)(Object)this;


        if (epicFight_CombatHotbar_Versions$shouldBlockHotbarChange(mc)) {
            return; // Cancel assignment
        }

        inventory.selected = value;
    }

    @Unique
    private boolean epicFight_CombatHotbar_Versions$shouldBlockHotbarChange(Minecraft mc) {
        return CombatModeHandler.isInBattleMode(mc.player);
    }
}