package me.basiqueevangelist.commonbridge.opac;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.CommonProtection;
import eu.pb4.common.protection.api.ProtectionProvider;
import me.basiqueevangelist.commonbridge.CommonBridge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.player.config.api.PlayerConfigOptions;

public class OpacProtectionProvider implements ProtectionProvider {
    public static final Identifier ID = CommonBridge.id("opac");
    public static final OpacProtectionProvider INSTANCE = new OpacProtectionProvider();

    public static void init() {
        CommonProtection.register(ID, INSTANCE);
    }

    @Override
    public boolean isProtected(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld sw)) return false;

        return OpenPACServerAPI.get(sw.getServer()).getServerClaimsManager().get(world.getRegistryKey().getValue(), new ChunkPos(pos)) != null;
    }

    @Override
    public boolean isAreaProtected(World world, Box area) {
        if (!(world instanceof ServerWorld sw)) return false;

        int minCX = (int) Math.floor(area.minX / 16);
        int minCZ = (int) Math.floor(area.minX / 16);
        int maxCX = (int) Math.ceil(area.maxX / 16);
        int maxCZ = (int) Math.ceil(area.maxZ / 16);
        var mgr = OpenPACServerAPI.get(sw.getServer()).getServerClaimsManager();

        for (int cx = minCX; cx < maxCX; cx++) {
            for (int cz = minCZ; cz < maxCZ; cz++) {
                if (mgr.get(world.getRegistryKey().getValue(), cx, cz) != null)
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean canBreakBlock(World world, BlockPos pos, GameProfile profile, PlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return true;

        player = CommonBridge.tryResolveProtectionPlayer(sw, profile, player);

        if (player == null) return true;

        return !OpenPACServerAPI.get(sw.getServer())
            .getChunkProtection()
            .onBlockInteraction(player, null, null, sw, pos, Direction.UP, true, false);
    }

    @Override
    public boolean canExplodeBlock(World world, BlockPos pos, Explosion explosion, GameProfile profile, PlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return true;

        player = CommonBridge.tryResolveProtectionPlayer(sw, profile, player);

        var api = OpenPACServerAPI.get(sw.getServer());

        if (player != null && OpacHacks.hasActiveFullPass(api.getChunkProtection(), player)) return true;

        var claim = api.getServerClaimsManager().get(world.getRegistryKey().getValue(), new ChunkPos(pos));
        var cfg = api.getChunkProtection().getClaimConfig(claim);

        return !(cfg.getEffective(PlayerConfigOptions.PROTECT_CLAIMED_CHUNKS)
              && cfg.getEffective(PlayerConfigOptions.PROTECT_CLAIMED_CHUNKS_BLOCKS_FROM_EXPLOSIONS));
    }

    @Override
    public boolean canPlaceBlock(World world, BlockPos pos, GameProfile profile, PlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return true;

        player = CommonBridge.tryResolveProtectionPlayer(sw, profile, player);

        if (player == null) return true;

        return !OpenPACServerAPI.get(sw.getServer())
            .getChunkProtection()
            .onEntityPlaceBlock(player, sw, pos);
    }

    @Override
    public boolean canInteractBlock(World world, BlockPos pos, GameProfile profile, PlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return true;

        player = CommonBridge.tryResolveProtectionPlayer(sw, profile, player);

        if (player == null) return true;

        return !OpenPACServerAPI.get(sw.getServer())
            .getChunkProtection()
            .onBlockInteraction(player, null, null, sw, pos, Direction.UP, false, false);
    }

    @Override
    public boolean canInteractEntity(World world, Entity entity, GameProfile profile, PlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return true;

        player = CommonBridge.tryResolveProtectionPlayer(sw, profile, player);

        if (player == null) return true;

        return !OpenPACServerAPI.get(sw.getServer())
            .getChunkProtection()
            .onEntityInteraction(player, player, entity, null, null, false, false);
    }

    @Override
    public boolean canDamageEntity(World world, Entity entity, GameProfile profile, PlayerEntity player) {
        if (!(world instanceof ServerWorld sw)) return true;

        player = CommonBridge.tryResolveProtectionPlayer(sw, profile, player);

        if (player == null) return true;

        return !OpenPACServerAPI.get(sw.getServer())
            .getChunkProtection()
            .onEntityInteraction(player, player, entity, null, null, true, false);
    }
}
