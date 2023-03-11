package me.basiqueevangelist.commonbridge.opac;

import net.minecraft.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xaero.pac.common.server.claims.protection.api.IChunkProtectionAPI;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class OpacHacks {
    private static final Logger LOGGER = LoggerFactory.getLogger("CommonBridge/OpacHacks");
    private static final MethodHandle HAS_ACTIVE_FULL_PASS = getHasActiveFullPass();

    private OpacHacks() {

    }

    public static boolean hasActiveFullPass(IChunkProtectionAPI chunkProtection, Entity entity) {
        try {
            return (boolean) HAS_ACTIVE_FULL_PASS.invokeExact((IChunkProtectionAPI) chunkProtection, (Entity) entity);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle getHasActiveFullPass() {
        try {
            var klass = Class.forName("xaero.pac.common.server.claims.protection.ChunkProtection");
            var method = klass.getDeclaredMethod("hasActiveFullPass", Entity.class);
            method.setAccessible(true);
            return MethodHandles.lookup().unreflect(method)
                .asType(MethodType.methodType(boolean.class, IChunkProtectionAPI.class, Entity.class));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            LOGGER.warn("Couldn't hack into OPAC's hasActiveFullPass method!", e);

            return MethodHandles.empty(MethodType.methodType(boolean.class, IChunkProtectionAPI.class, Entity.class));
        }
    }
}
