package me.basiqueevangelist.commonbridge.numismatic;

import com.glisco.numismaticoverhaul.ModComponents;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnlinePurseAccount extends PurseAccount {
    private final ServerPlayerEntity player;

    public OnlinePurseAccount(ServerPlayerEntity player) {
        super(player.getUuid());
        this.player = player;
    }

    @Override
    public long balance() {
        return ModComponents.CURRENCY.get(player).getValue();
    }

    @Override
    public void setBalance(long value) {
        ModComponents.CURRENCY.get(player).modify(value - balance());
    }

    @Override
    public String playerName() {
        return player.getEntityName();
    }
}
