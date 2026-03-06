package net.kenji.epic_fight_combat_hotbar.capability.handlers;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID)
public class PlayerDeathHandler {

    public static class PlayerDeathStorage{
        public static Map<UUID, PlayerDeathStorage> playerDeathStorageMap = new HashMap<>();
        public static PlayerDeathStorage get(Player player){
            return playerDeathStorageMap.computeIfAbsent(player.getUUID(), k -> new PlayerDeathStorage());
        }
        List<ItemStack> stacks = new ArrayList<>(4);
    }

    // Use HIGHEST priority so we add items BEFORE death mods collect them
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {

            // Don't drop if keepInventory is enabled
            if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                List<ItemStack> storedStacks = new ArrayList<>();
                for(int i = 0; i < handler.getSlots(); i++){
                    storedStacks.add(handler.getStackInSlot(i));
                }
                PlayerDeathStorage.get(player).stacks = storedStacks;
                return;
            }

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    // Add to drops - death mods will pick these up automatically
                    ItemEntity itemEntity = new ItemEntity(
                            player.level(),
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            stack.copy()
                    );
                    itemEntity.setDefaultPickUpDelay();
                    event.getDrops().add(itemEntity);

                    // Clear the slot after adding to drops
                    handler.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        });
    }

    // Handle respawn - copy items if keepInventory is true
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player newPlayer = event.getEntity();

        // If keepInventory is enabled, copy items over
        if (newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            newPlayer.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
                for (int i = 0; i < PlayerDeathStorage.get(newPlayer).stacks.size(); i++) {
                    handler.setStackInSlot(i, PlayerDeathStorage.get(newPlayer).stacks.get(i));
                }
            });
        }
    }
}