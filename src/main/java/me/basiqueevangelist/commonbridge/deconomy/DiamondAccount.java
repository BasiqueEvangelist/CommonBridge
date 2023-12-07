package me.basiqueevangelist.commonbridge.deconomy;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import eu.pb4.common.economy.api.EconomyTransaction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class DiamondAccount implements EconomyAccount {
    public static final Identifier ID = new Identifier("diamondeconomy", "account");
    private final UUID playerId;
    private final String playerIdString;

    public DiamondAccount(UUID playerId) {
        this.playerId = playerId;
        this.playerIdString = playerId.toString();
    }

    @Override
    public Text name() {
        // TODO: choose better name:tm:
        return Text.literal("Account");
    }

    @Override
    public UUID owner() {
        return playerId;
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public long balance() {
        return DiamondUtils.getDatabaseManager().getBalanceFromUUID(playerIdString);
    }

    @Override
    public EconomyTransaction canIncreaseBalance(long value) {
        int current = DiamondUtils.getDatabaseManager().getBalanceFromUUID(playerIdString);

        long newBalance = ((long) current) + value;

        if (newBalance > Integer.MAX_VALUE) {
            return new EconomyTransaction.Simple(
                false,
                Text.literal("Integer overflow ($" + newBalance + " > $" + Integer.MAX_VALUE + ")"),
                current,
                current,
                0,
                this
            );
        }

        return new EconomyTransaction.Simple(
                true,
                Text.literal("Added $" + value + " to the account"),
                newBalance,
                current,
                value,
                this
        );
    }

    @Override
    public EconomyTransaction canDecreaseBalance(long value) {
        int current = DiamondUtils.getDatabaseManager().getBalanceFromUUID(playerIdString);

        long newBalance = ((long) current) - value;

        if (newBalance < 0) {
            return new EconomyTransaction.Simple(
                    false,
                    Text.literal("Not enough money ($" + current + " < $" + value + ")"),
                    current,
                    current,
                    0,
                    this
            );
        }

        return new EconomyTransaction.Simple(
                true,
                Text.literal("Removed $" + value + " from the account"),
                newBalance,
                current,
                value,
                this
        );
    }

    @Override
    public void setBalance(long value) {
        DiamondUtils.getDatabaseManager().setBalance(playerIdString, Math.toIntExact(value));
    }

    @Override
    public EconomyProvider provider() {
        return DiamondEconomyProvider.INSTANCE;
    }

    @Override
    public EconomyCurrency currency() {
        return DollarsCurrency.INSTANCE;
    }

    @Override
    public ItemStack accountIcon() {
        return Items.DIAMOND.getDefaultStack();
    }
}
