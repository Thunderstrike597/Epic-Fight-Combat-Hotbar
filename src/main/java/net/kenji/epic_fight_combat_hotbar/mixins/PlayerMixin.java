package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.api.CombatHotbarHandler;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.kenji.epic_fight_combat_hotbar.client.HotbarSlotHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {



    @Inject(method = "getItemBySlot", at = @At("RETURN"), cancellable = true)
    private void getCombatHotbarItem(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir) {
        Player player = (Player) (Object) this;
        if (!CombatModeHandler.isInBattleMode(player)) {
            return;
        }
        CombatHotbarHandler handler = CombatHotbarProvider.getCombatHotbarCap(player);

        int selectedSlot = HotbarSlotHandler.getSelectedSlot(player);
        ItemStack stack = handler.getStackInSlot(selectedSlot);
        ItemStack originalReturn = cir.getReturnValue();
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            if(originalReturn != null)
                handler.setOriginalMainHandStack(originalReturn);
            cir.setReturnValue(stack);
        }
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void preventVanillaSlotOverwrite(EquipmentSlot pSlot, ItemStack pStack, CallbackInfo ci) {
        if (pSlot != EquipmentSlot.MAINHAND) return;

        Player player = (Player)(Object)this;
        if (!CombatModeHandler.isInBattleMode(player)) return;

        // In combat mode, prevent vanilla from setting the mainhand
        // Instead, sync to our combat hotbar
        CombatHotbarHandler handler = CombatHotbarProvider.getCombatHotbarCap(player);
        int selectedSlot = HotbarSlotHandler.getSelectedSlot(player);
        handler.setStackInSlot(selectedSlot, pStack.copy());

        // Cancel the vanilla set operation
        ci.cancel();
    }
}