package net.kenji.epic_fight_combat_hotbar.capability.handlers;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID)
public class ItemTagHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        for(int i = 0; i < player.inventoryMenu.slots.size() - CombatHotbarProvider.SLOTS; i++){
            if(player.inventoryMenu.getSlot(i).getItem().getTag() != null){
                if(Objects.requireNonNull(player.inventoryMenu.getSlot(i).getItem().getTag()).contains(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG)){
                    Objects.requireNonNull(player.inventoryMenu.getSlot(i).getItem().getTag()).remove(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG);
                }
            }
        }
        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent((handler -> {
            for(int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
                if(!handler.getStackInSlot(i).isEmpty()){
                    if(handler.getStackInSlot(i).getTag() != null && !handler.getStackInSlot(i).getTag().contains(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG)){
                        handler.getStackInSlot(i).getTag().putInt(CombatHotbarProvider.COMBAT_HOTBAR_SLOT_ITEM_TAG, i);
                    }
                }
            }
        }));
    }
}