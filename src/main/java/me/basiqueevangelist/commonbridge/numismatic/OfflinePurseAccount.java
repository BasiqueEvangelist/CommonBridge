package me.basiqueevangelist.commonbridge.numismatic;

import com.mojang.authlib.GameProfile;
import io.wispforest.owo.offline.OfflineDataLookup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class OfflinePurseAccount extends PurseAccount {
    private final MinecraftServer server;

    public OfflinePurseAccount(UUID ownerId, MinecraftServer server) {
        super(ownerId);
        this.server = server;
    }

    @Override
    public String playerName() {
        return server.getUserCache().getByUuid(ownerId).map(GameProfile::getName).orElse(ownerId.toString());
    }

    @Override
    public long balance() {
        return OfflineDataLookup.get(ownerId)
            .getCompound("cardinal_components")
            .getCompound("numismatic-overhaul:currency")
            .getLong("Value");
    }

    @Override
    public void setBalance(long value) {
        NbtCompound tag = OfflineDataLookup.get(ownerId);

        var ccaTag = tag.getCompound("cardinal_components");
        tag.put("cardinal_components", ccaTag);

        var currencyComponent = ccaTag.getCompound("numismatic-overhaul:currency");
        ccaTag.put("numismatic-overhaul:currency", currencyComponent);

        currencyComponent.putLong("Value", value);

        OfflineDataLookup.put(ownerId, tag);
    }
}
