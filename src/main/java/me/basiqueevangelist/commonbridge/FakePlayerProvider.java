package me.basiqueevangelist.commonbridge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class FakePlayerProvider {
    private static LoadingCache<GameProfile, ServerPlayerEntity> FAKE_PLAYERS = null;
    private static int tickCount = 0;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            FAKE_PLAYERS = CacheBuilder.newBuilder()
                .initialCapacity(64)
                .expireAfterWrite(
                    30, TimeUnit.SECONDS)
                .softValues()
                .build(CacheLoader.from((profile) -> new ServerPlayerEntity(server, server.getOverworld(), profile)));
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCount++;

            if (tickCount % 16 == 0)
                FAKE_PLAYERS.cleanUp();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            FAKE_PLAYERS.invalidateAll();
            FAKE_PLAYERS = null;
        });
    }

    public static @Nullable ServerPlayerEntity tryResolveProtectionPlayer(World w, GameProfile profile, PlayerEntity old) {
        if (old instanceof ServerPlayerEntity spe)
            return spe;

        if (!(w instanceof ServerWorld sw))
            return null;

        ServerPlayerEntity online = sw.getServer().getPlayerManager().getPlayer(profile.getId());

        if (online != null) return online;

        return FAKE_PLAYERS.getUnchecked(profile);
    }
}
