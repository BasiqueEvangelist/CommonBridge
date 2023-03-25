package me.basiqueevangelist.commonbridge.mixin.opac;

import me.basiqueevangelist.commonbridge.asm.DevOnly;
import me.basiqueevangelist.commonbridge.asm.OnlyWithMod;
import me.basiqueevangelist.commonbridge.asm.SystemPropertyControlled;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xaero.pac.common.server.claims.protection.ChunkProtectionEntityHelper;

@OnlyWithMod("openpartiesandclaims")
@DevOnly
@SystemPropertyControlled("commonBridge.mixins.fixOpenPAC")
@Pseudo
@Mixin(ChunkProtectionEntityHelper.class)
public class ChunkProtectionEntityHelperMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(stringValue = "field_17951"))
    private static String useProperFieldName1(String constant) {
        return FabricLoader
            .getInstance()
            .getMappingResolver()
            .mapFieldName("intermediary",  "net.minecraft.class_4019", "field_17951", "Lnet/minecraft/class_2940;");
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(stringValue = "field_17952"))
    private static String useProperFieldName2(String constant) {
        return FabricLoader
            .getInstance()
            .getMappingResolver()
            .mapFieldName("intermediary",  "net.minecraft.class_4019", "field_17952", "Lnet/minecraft/class_2940;");
    }
}
