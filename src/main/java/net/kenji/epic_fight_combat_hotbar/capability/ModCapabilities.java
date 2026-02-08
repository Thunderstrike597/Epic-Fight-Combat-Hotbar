package net.kenji.epic_fight_combat_hotbar.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.ItemStackHandler;

public class ModCapabilities {
    public static final Capability<ItemStackHandler> COMBAT_HOTBAR = 
        CapabilityManager.get(new CapabilityToken<>(){});
}