package net.kenji.epic_fight_combat_hotbar.events;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.kenji.epic_fight_combat_hotbar.capability.ModCapabilities;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EpicFightCombatHotbar.MODID)
public class PlayerDeathHandler {

    // Use HIGHEST priority so we add items BEFORE death mods collect them
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Don't drop if keepInventory is enabled
        if (player.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_KEEPINVENTORY)) {
            return;
        }

        player.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(handler -> {
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
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        // Only on death, not dimension change
        if (!event.isWasDeath()) {
            return;
        }

        // If keepInventory is enabled, copy items over
        if (oldPlayer.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_KEEPINVENTORY)) {
            oldPlayer.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(oldHandler -> {
                newPlayer.getCapability(ModCapabilities.COMBAT_HOTBAR).ifPresent(newHandler -> {
                    for (int i = 0; i < oldHandler.getSlots(); i++) {
                        newHandler.setStackInSlot(i, oldHandler.getStackInSlot(i).copy());
                    }
                });
            });
        }
    }
}