package net.kenji.epic_fight_combat_hotbar.capability;

import net.kenji.epic_fight_combat_hotbar.EpicFightCombatHotbar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "epic_fight_combat_hotbar")
public class CapabilityEvents {

    private static final ResourceLocation COMBAT_HOTBAR_CAP =
            new ResourceLocation(EpicFightCombatHotbar.MODID, "combat_hotbar");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            CombatHotbarProvider provider = new CombatHotbarProvider();
            event.addCapability(COMBAT_HOTBAR_CAP, provider);

            // Listen for capability invalidation
            event.addListener(provider::invalidate);
        }
    }
}