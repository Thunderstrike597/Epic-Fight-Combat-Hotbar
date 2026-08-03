package net.kenji.epic_fight_combat_hotbar.client;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.api.Pair;
import net.kenji.epic_fight_combat_hotbar.capability.CombatHotbarProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HotbarInputHandler {
    
    // Handle number key presses
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
       if(Minecraft.getInstance().player != null) {
           if (!CombatModeHandler.isInBattleMode(Minecraft.getInstance().player)) {
               return;
           }
           Minecraft mc = Minecraft.getInstance();
           if (mc.player == null || mc.screen != null) {
               return;
           }
           List<Pair<KeyMapping, Integer>> keys = new ArrayList<>();
           for(int i = 0; i < CombatHotbarProvider.SLOTS; i++) {
               keys.add(new Pair<>(mc.options.keyHotbarSlots[i], i));
           }

           for(Pair<KeyMapping, Integer> key : keys) {
               if (key.getKey().isDown()) {
                   int slot = key.getValue();
                   HotbarSlotHandler.setSelectedSlotSynchronised(mc.player, slot);
               }
           }
       }
    }
}