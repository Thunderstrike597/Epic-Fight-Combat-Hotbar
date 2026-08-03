package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.kenji.epic_fight_combat_hotbar.client.HotbarSlotHandler;
import net.minecraft.client.Minecraft;
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
        if (!CombatModeHandler.isInBattleMode(Minecraft.getInstance().player)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }

        int currentSlot = HotbarSlotHandler.getSelectedSlot(mc.player);

        if (pDirection > 0) {
            // Scroll up - previous slot
            currentSlot--;
            if (currentSlot < 0) currentSlot = 3;
        } else if (pDirection < 0) {
            // Scroll down - next slot
            currentSlot++;
            if (currentSlot > 3) currentSlot = 0;
        }

        HotbarSlotHandler.setSelectedSlotSynchronised(mc.player, currentSlot);
        ci.cancel();
    }

}
