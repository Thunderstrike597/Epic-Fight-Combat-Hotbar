package net.kenji.epic_fight_combat_hotbar.client;

import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CombatModeHandler {
    
    public static boolean isInBattleMode(Player player) {
        if (player == null) return false;
        
        // Get Epic Fight's player capability
        return player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).map(entityPatch -> {
            if (entityPatch instanceof PlayerPatch<?> playerPatch) {
                return playerPatch.isEpicFightMode();
            }
            return false;
        }).orElse(false);
    }
}