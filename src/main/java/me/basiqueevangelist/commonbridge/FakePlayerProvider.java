package me.basiqueevangelist.commonbridge;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FakePlayerProvider {
    public static @Nullable ServerPlayerEntity tryResolveProtectionPlayer(World w, GameProfile profile, PlayerEntity old) {
        if (old instanceof ServerPlayerEntity spe)
            return spe;

        if (!(w instanceof ServerWorld sw))
            return null;

        ServerPlayerEntity online = sw.getServer().getPlayerManager().getPlayer(profile.getId());

        if (online != null) return online;

        return FakePlayer.get(sw, profile);
    }
}
