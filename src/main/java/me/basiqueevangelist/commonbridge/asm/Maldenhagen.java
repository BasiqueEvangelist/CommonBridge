package me.basiqueevangelist.commonbridge.asm;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Maldenhagen implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("CommonBridge/Maldenhagen");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            ClassNode cn = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
            AnnotationNode modAnnot = Annotations.getInvisible(cn, OnlyWithMod.class);

            if (modAnnot != null && !FabricLoader.getInstance().isModLoaded(Annotations.getValue(modAnnot, "value")))
                return false;

            AnnotationNode devOnlyAnnot = Annotations.getInvisible(cn, DevOnly.class);

            if (devOnlyAnnot != null && !FabricLoader.getInstance().isDevelopmentEnvironment())
                return false;

            AnnotationNode propAnnot = Annotations.getInvisible(cn, SystemPropertyControlled.class);

            if (propAnnot != null) {
                boolean enabled = Annotations.getValue(propAnnot, "defaultValue", (Boolean) true);

                try {
                    enabled = Boolean.parseBoolean(System.getProperty(Annotations.getValue(propAnnot, "value")));
                } catch (IllegalArgumentException | NullPointerException e) {
                    // ...
                }

                if (!enabled)
                    return false;
            }

            AnnotationNode infoAnnot = Annotations.getInvisible(cn, InfoOnEnabled.class);

            if (infoAnnot != null) {
                LOGGER.info(Annotations.getValue(infoAnnot, "value"));
            }
        } catch (IOException | ClassNotFoundException e) {
            // ...
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
