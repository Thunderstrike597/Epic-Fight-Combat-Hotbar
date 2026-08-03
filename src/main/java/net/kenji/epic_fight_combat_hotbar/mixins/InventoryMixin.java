package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "swapPaint", at = @At("HEAD"), cancellable = true)
    public void interceptMouseScroll(double pDirection, CallbackInfo ci){
        Inventory self = (Inventory)(Object)this;
        if(CombatModeHandler.isInBattleMode(self.player)){
            ci.cancel();
        }
    }

}
