package me.basiqueevangelist.commonbridge.mixin.opac;

import me.basiqueevangelist.commonbridge.asm.DevOnly;
import me.basiqueevangelist.commonbridge.asm.OnlyWithMod;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xaero.pac.common.server.core.ServerCore;

@OnlyWithMod("openpartiesandclaims")
@DevOnly
@Mixin(ServerCore.class)
public class ServerCoreMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(stringValue = "field_5963"))
    private static String useProperFieldName(String constant) {
        return FabricLoader
            .getInstance()
            .getMappingResolver()
            .mapFieldName("intermediary",  "net.minecraft.class_1297", "field_5963", "Z");
    }
}
