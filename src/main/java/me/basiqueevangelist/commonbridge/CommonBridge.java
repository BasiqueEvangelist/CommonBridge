package me.basiqueevangelist.commonbridge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.commonbridge.ftbchunks.FtbChunksProtectionProvider;
import me.basiqueevangelist.commonbridge.numismatic.NumismaticEconomyProvider;
import me.basiqueevangelist.commonbridge.opac.OpacProtectionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.concurrent.TimeUnit;

public class CommonBridge implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("numismatic-overhaul"))
            NumismaticEconomyProvider.init();

        if (FabricLoader.getInstance().isModLoaded("ftbchunks"))
            FtbChunksProtectionProvider.init();

        if (FabricLoader.getInstance().isModLoaded("openpartiesandclaims"))
            OpacProtectionProvider.init();

        FakePlayerProvider.init();
    }

    public static Identifier id(String path) {
        return new Identifier("common-bridge", path);
    }
}
