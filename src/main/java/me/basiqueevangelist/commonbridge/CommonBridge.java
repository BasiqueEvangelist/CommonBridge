package me.basiqueevangelist.commonbridge;

import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.commonbridge.ftbchunks.FtbChunksProtectionProvider;
import me.basiqueevangelist.commonbridge.numismatic.NumismaticEconomyProvider;
import me.basiqueevangelist.commonbridge.opac.OpacProtectionProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CommonBridge implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("numismatic-overhaul"))
            NumismaticEconomyProvider.init();

        if (FabricLoader.getInstance().isModLoaded("ftbchunks"))
            FtbChunksProtectionProvider.init();

        if (FabricLoader.getInstance().isModLoaded("openpartiesandclaims"))
            OpacProtectionProvider.init();
    }

    public static Identifier id(String path) {
        return new Identifier("common-bridge", path);
    }

    public static @Nullable ServerPlayerEntity tryResolveProtectionPlayer(World w, GameProfile profile, PlayerEntity old) {
        if (old instanceof ServerPlayerEntity spe)
            return spe;

        if (!(w instanceof ServerWorld sw))
            return null;

        ServerPlayerEntity online = sw.getServer().getPlayerManager().getPlayer(profile.getId());

        if (online != null) return online;

        return new ServerPlayerEntity(sw.getServer(), sw, profile);
    }
}
