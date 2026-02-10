package net.kenji.epic_fight_combat_hotbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)
public class CombatModeHandler {
    
    public static boolean isInBattleMode() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
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