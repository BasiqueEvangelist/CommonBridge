package me.basiqueevangelist.commonbridge.ftbchunks;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbchunks.FTBChunksExpected;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.data.Protection;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import eu.pb4.common.protection.api.CommonProtection;
import eu.pb4.common.protection.api.ProtectionProvider;
import me.basiqueevangelist.commonbridge.CommonBridge;
import me.basiqueevangelist.commonbridge.FakePlayerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class FtbChunksProtectionProvider implements ProtectionProvider {
    public static final Identifier ID = CommonBridge.id("ftbchunks");
    public static final FtbChunksProtectionProvider INSTANCE = new FtbChunksProtectionProvider();

    public static void init() {
        CommonProtection.register(ID, INSTANCE);
    }



    @Override
    public boolean isProtected(World world, BlockPos pos) {
        if (FTBChunksWorldConfig.DISABLE_PROTECTION.get()) return false;

        return FTBChunksAPI.getManager().getChunk(new ChunkDimPos(world, pos)) != null;
    }

    @Override
    public boolean isAreaProtected(World world, Box area) {
        if (FTBChunksWorldConfig.DISABLE_PROTECTION.get()) return false;

        int minCX = (int) Math.floor(area.minX / 16);
        int minCZ = (int) Math.floor(area.minX / 16);
        int maxCX = (int) Math.ceil(area.maxX / 16);
        int maxCZ = (int) Math.ceil(area.maxZ / 16);

        for (var chunk : FTBChunksAPI.getManager().getAllClaimedChunks()) {
            if (chunk.pos.x >= minCX && chunk.pos.x < maxCX
             && chunk.pos.z >= minCZ && chunk.pos.z < maxCZ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canBreakBlock(World world, BlockPos pos, GameProfile profile, PlayerEntity player) {
        player = FakePlayerProvider.tryResolveProtectionPlayer(world, profile, player);

        if (player == null) return true;

        return !FTBChunksAPI.getManager().protect(player, Hand.MAIN_HAND, pos, FTBChunksExpected.getBlockBreakProtection(), null);
    }

    @Override
    public boolean canExplodeBlock(World world, BlockPos pos, Explosion explosion, GameProfile profile, PlayerEntity player) {
        ChunkDimPos chunkPos = new ChunkDimPos(world, pos);
        ClaimedChunk chunk = FTBChunksAPI.getManager().getChunk(chunkPos);

        return chunk == null || chunk.allowExplosions();
    }

    @Override
    public boolean canPlaceBlock(World world, BlockPos pos, GameProfile profile, PlayerEntity player) {
        player = FakePlayerProvider.tryResolveProtectionPlayer(world, profile, player);

        if (player == null) return true;

        return !FTBChunksAPI.getManager().protect(player, Hand.MAIN_HAND, pos, FTBChunksExpected.getBlockPlaceProtection(), null);
    }

    @Override
    public boolean canInteractBlock(World world, BlockPos pos, GameProfile profile, PlayerEntity player) {
        player = FakePlayerProvider.tryResolveProtectionPlayer(world, profile, player);

        if (player == null) return true;

        return !FTBChunksAPI.getManager().protect(player, Hand.MAIN_HAND, pos, FTBChunksExpected.getBlockInteractProtection(), null);

    }

    @Override
    public boolean canInteractEntity(World world, Entity entity, GameProfile profile, PlayerEntity player) {
        player = FakePlayerProvider.tryResolveProtectionPlayer(world, profile, player);

        if (player == null) return true;

        return !FTBChunksAPI.getManager().protect(player, Hand.MAIN_HAND, entity.getBlockPos(), Protection.INTERACT_ENTITY, entity);
    }

    @Override
    public boolean canDamageEntity(World world, Entity entity, GameProfile profile, PlayerEntity player) {
        if (entity instanceof LivingEntity) return true;

        player = FakePlayerProvider.tryResolveProtectionPlayer(world, profile, player);

        if (player == null) return true;

        return !FTBChunksAPI.getManager().protect(player, Hand.MAIN_HAND, entity.getBlockPos(), Protection.ATTACK_NONLIVING_ENTITY, entity);
    }
}
