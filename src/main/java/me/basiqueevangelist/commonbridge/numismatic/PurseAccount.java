package me.basiqueevangelist.commonbridge.numismatic;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import eu.pb4.common.economy.api.EconomyTransaction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public abstract class PurseAccount implements EconomyAccount {
    public static final Identifier ID = NumismaticOverhaul.id("purse");

    protected final UUID ownerId;

    public PurseAccount(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public abstract String playerName();

    @Override
    public Text name() {
        return Text.literal(playerName() + "'s Purse");
    }

    public UUID owner() {
        return ownerId;
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public EconomyTransaction canIncreaseBalance(long value) {
        long current = balance();

        return new EconomyTransaction.Simple(
            true,
            Text.literal("Added ¢" + value + " to " + playerName() + "'s account"),
            current + value,
            current,
            value,
            this
        );
    }

    @Override
    public EconomyTransaction canDecreaseBalance(long value) {
        long current = balance();

        if (current < value) {
            return new EconomyTransaction.Simple(
                true,
                Text.literal(playerName() + " doesn't have enough coins! (¢" + current + " < ¢" + value + ")"),
                current - value,
                current,
                value,
                this
            );
        } else {
            return new EconomyTransaction.Simple(
                true,
                Text.literal("Removed ¢" + value + " from " + playerName() + "'s account"),
                current - value,
                current,
                value,
                this
            );
        }
    }

    @Override
    public EconomyProvider provider() {
        return NumismaticEconomyProvider.INSTANCE;
    }

    @Override
    public EconomyCurrency currency() {
        return CoinsCurrency.INSTANCE;
    }
}
