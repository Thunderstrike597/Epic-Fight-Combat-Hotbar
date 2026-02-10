package net.kenji.epic_fight_combat_hotbar.mixins;

import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    public void renderItem2(LivingEntity pEntity, Level pLevel, ItemStack pStack, int pX, int pY, int pSeed, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    public void renderItem3(LivingEntity pEntity, Level pLevel, ItemStack pStack, int pX, int pY, int pSeed, int pGuiOffset, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    public void renderItem4(ItemStack pStack, int pX, int pY, int pSeed, int pGuiOffset, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    public void renderItem5(ItemStack pStack, int pX, int pY, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }
    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"), cancellable = true)
    public void renderItem6(ItemStack pStack, int pX, int pY, int pSeed, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"), cancellable = true)
    public void renderItem7(LivingEntity pEntity, Level pLevel, ItemStack pStack, int pX, int pY, int pSeed, int pGuiOffset, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }
    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    public void cancelTooltipRender(Font pFont, ItemStack pStack, int pMouseX, int pMouseY, CallbackInfo ci){
        epicFight_CombatHotbar_Versions$cancelCreativeRender(pStack, ci);
    }

    @Unique
    private void epicFight_CombatHotbar_Versions$cancelCreativeRender(ItemStack pStack, CallbackInfo ci){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;
        if (!(mc.screen instanceof AbstractContainerScreen<?>)) {
            return;
        }
        if(player.isCreative()) {
            player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
                for (int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
                    if (pStack == handler.getStackInSlot(i)) {
                        ci.cancel();
                    }
                }
            });
        }
    }

}