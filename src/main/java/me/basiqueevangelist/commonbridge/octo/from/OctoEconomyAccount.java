package me.basiqueevangelist.commonbridge.octo.from;

import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.UniqueUser;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import eu.pb4.common.economy.api.EconomyTransaction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class OctoEconomyAccount implements EconomyAccount {
    private static final Logger LOGGER = LoggerFactory.getLogger("CommonBridge/OctoEconomyAccount");

    private final UniqueUser user;
    private final OctoEconomyCurrency currency;

    public OctoEconomyAccount(UniqueUser user, OctoEconomyCurrency currency) {
        this.user = user;
        this.currency = currency;
    }

    @Override
    public Text name() {
        // todo: translations
        return Text.literal("")
            .append(currency.name())
            .append(" [Account]");
    }

    @Override
    public UUID owner() {
        return user.getUserID();
    }

    @Override
    public Identifier id() {
        return currency.id();
    }

    @Override
    public long balance() {
        return currency.fromOctoValue(user.getBalance(currency.wrapped()));
    }

    @Override
    public EconomyTransaction canIncreaseBalance(long value) {
        return new EconomyTransaction.Simple(
            true,
            Text.literal("common bridge: simulation not implemented, assuming yes"),
            balance() + value,
            balance(),
            value,
            this
        );
    }

    @Override
    public EconomyTransaction increaseBalance(long value) {
        var octo = user.depositMoney(currency.wrapped(), currency.toOctoValue(value), "common economy api transaction");

        return new EconomyTransaction.Simple(
            octo.getTransactionResponse() == Transaction.Response.SUCCESS,
            octo.getMessage() == null ? null : Text.literal(octo.getMessage()),
            currency.fromOctoValue(user.getBalance(currency.wrapped()) + octo.getTransactionDelta()),
            balance(),
            currency.fromOctoValue(octo.getTransactionDelta()),
            this
        );
    }

    @Override
    public EconomyTransaction canDecreaseBalance(long value) {
        if (user.hasAmount(currency.wrapped(), currency.toOctoValue(value))) {
            return new EconomyTransaction.Simple(
                false,
                Text.literal("common bridge: not enough money"),
                balance(),
                balance(),
                -value,
                this
            );
        }

        return new EconomyTransaction.Simple(
            true,
            Text.literal("common bridge: simulation not implemented, assuming yes"),
            balance() - value,
            balance(),
            -value,
            this
        );
    }

    @Override
    public EconomyTransaction decreaseBalance(long value) {
        if (!user.hasAmount(currency.wrapped(), currency.toOctoValue(value))) {
            return new EconomyTransaction.Simple(
                false,
                Text.literal("common bridge: not enough money"),
                balance(),
                balance(),
                -value,
                this
            );
        }

        var octo = user.withdrawMoney(currency.wrapped(), currency.toOctoValue(value), "common economy api transaction");

        return new EconomyTransaction.Simple(
            octo.getTransactionResponse() == Transaction.Response.SUCCESS,
            octo.getMessage() == null ? null : Text.literal(octo.getMessage()),
            currency.fromOctoValue(user.getBalance(currency.wrapped()) + octo.getTransactionDelta()),
            balance(),
            currency.fromOctoValue(octo.getTransactionDelta()),
            this
        );
    }

    @Override
    public void setBalance(long value) {
        var octo = user.setBalance(currency.wrapped(), currency.toOctoValue(value));

        if (octo.getTransactionResponse() != Transaction.Response.SUCCESS) {
            LOGGER.info("setBalance transaction failed {}'s account: {}", user.getIdentity(), octo.getMessage());
        }
    }

    @Override
    public EconomyProvider provider() {
        return OctoEconomyProvider.INSTANCE;
    }

    @Override
    public EconomyCurrency currency() {
        return currency;
    }

    @Override
    public ItemStack accountIcon() {
        return Items.INK_SAC.getDefaultStack();
    }
}
