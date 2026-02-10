package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.kenji.epic_fight_combat_hotbar.client.CombatModeHandler;
import net.kenji.epic_fight_combat_hotbar.client.HotbarRenderHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@Mixin(Player.class)
public class PlayerMixin {


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
                if(equipmentSlot == EquipmentSlot.MAINHAND)
                    cir.setReturnValue(stack);
            });
        }
    }
}