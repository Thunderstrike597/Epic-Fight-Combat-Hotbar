package net.kenji.epic_fight_combat_hotbar;

import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CompatMixinPlugin implements IMixinConfigPlugin {

    private boolean isLoaded(String modid) {
        return FMLLoader.getLoadingModList().getModFileById(modid) != null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        // Example: only load this mixin if "othermod" is installed
        if (mixinClassName.equals("net.kenji.epic_fight_combat_hotbar.mixins.compat.corpse.CorpseTransferItems")) {
            return isLoaded("corpse");
        }
        if (mixinClassName.equals("net.kenji.epic_fight_combat_hotbar.mixins.compat.corpse.CorpseInventoryContainerInvoker")) {
            return isLoaded("corpse");
        }
        if (mixinClassName.equals("net.kenji.epic_fight_combat_hotbar.mixins.compat.corpse.CorpseContainerBaseAccessor")) {
            return isLoaded("corpse");
        }
        return true;
    }

    // unused methods:
    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> a, Set<String> b) {}
    @Override public List<String> getMixins() { return null; }
    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
