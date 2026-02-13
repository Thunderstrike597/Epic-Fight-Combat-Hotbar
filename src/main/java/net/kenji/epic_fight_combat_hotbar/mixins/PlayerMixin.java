package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.kenji.epic_fight_combat_hotbar.client.HotbarRenderHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@Mixin(Player.class)
public abstract class PlayerMixin {


    @Shadow
    public abstract SlotAccess getSlot(int pSlot);

    @Inject(method = "getItemBySlot", at = @At("RETURN"), cancellable = true)
    private void getCombatHotbarItem(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object)this;
        if(livingEntity instanceof Player player) {
            if (!CombatModeHandler.isInBattleMode(player)) {
                return;
            }
            player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
                int selectedSlot = HotbarRenderHandler.getSelectedSlot();
                ItemStack stack = handler.getStackInSlot(selectedSlot);
                if(equipmentSlot == EquipmentSlot.MAINHAND) {
                        cir.setReturnValue(stack);
                }
            });
        }
    }
    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void preventVanillaSlotOverwrite(EquipmentSlot pSlot, ItemStack pStack, CallbackInfo ci) {
        if (pSlot != EquipmentSlot.MAINHAND) return;

        Player player = (Player)(Object)this;
        if (!CombatModeHandler.isInBattleMode(player)) return;

        // In combat mode, prevent vanilla from setting the mainhand
        // Instead, sync to our combat hotbar
        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
            int selectedSlot = HotbarRenderHandler.getSelectedSlot();
            handler.setStackInSlot(selectedSlot, pStack.copy());
        });

        // Cancel the vanilla set operation
        ci.cancel();
    }
}