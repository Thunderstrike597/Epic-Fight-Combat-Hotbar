package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.api.CombatHotbarHandler;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.kenji.epic_fight_combat_hotbar.client.HotbarSlotHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {



    @Inject(method = "getItemInHand", at = @At("RETURN"), cancellable = true)
    private void getCombatHotbarItem(InteractionHand pHand, CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object)this;
        if(livingEntity instanceof Player player) {
            if (!CombatModeHandler.isInBattleMode(player)) {
                return;
            }
            if (pHand != InteractionHand.MAIN_HAND) return;
            CombatHotbarHandler handler = CombatHotbarProvider.getCombatHotbarCap(player);
            ItemStack originalReturn = cir.getReturnValue();

            int selectedSlot = HotbarSlotHandler.getSelectedSlot(player);
            ItemStack stack = handler.getStackInSlot(selectedSlot);
            if(originalReturn != null)
                handler.setOriginalMainHandStack(originalReturn);

            cir.setReturnValue(stack);
        }
    }
}