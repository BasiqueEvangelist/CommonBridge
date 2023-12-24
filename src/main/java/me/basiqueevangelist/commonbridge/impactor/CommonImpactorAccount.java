package me.basiqueevangelist.commonbridge.impactor;

import eu.pb4.common.economy.api.EconomyAccount;
import me.basiqueevangelist.commonbridge.util.CurrencyUtils;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CommonImpactorAccount implements Account {
    private final EconomyAccount account;
    private final CommonImpactorCurrency currency;

    public CommonImpactorAccount(EconomyAccount account, CommonImpactorCurrency currency) {
        this.account = account;
        this.currency = currency;
    }

    @Override
    public @NotNull Currency currency() {
        return currency;
    }

    @Override
    public @NotNull UUID owner() {
        return account.owner();
    }

    @Override
    public boolean virtual() {
        return false;
    }

    @Override
    public @NotNull BigDecimal balance() {
        return CurrencyUtils.toBigDecimal(currency.wrapping(), account.balance());
    }

    @Override
    public @NotNull EconomyTransaction set(BigDecimal amount) {
        var tx = TransactionFactory.transaction(this, amount, EconomyTransactionType.SET);

        long value = CurrencyUtils.fromBigDecimal(currency.wrapping(), amount);

//        try {
//            value = amount.longValueExact();
//        } catch (ArithmeticException arith) {
//            // todo: use translations.
//            return tx.invalid(Text.literal("Common Bridge: couldn't convert amount to long"));
//        }

        account.setBalance(value);

        return tx.success(null);
    }

    @Override
    public @NotNull EconomyTransaction withdraw(BigDecimal amount) {
        var tx = TransactionFactory.transaction(this, amount, EconomyTransactionType.WITHDRAW);

        long value = CurrencyUtils.fromBigDecimal(currency.wrapping(), amount);

//        try {
//            value = amount.longValueExact();
//        } catch (ArithmeticException arith) {
//            // todo: use translations.
//            return tx.invalid(Text.literal("Common Bridge: couldn't convert amount to long"));
//        }

        var commonTx = account.decreaseBalance(value);

        if (commonTx.isSuccessful()) {
            return tx.success(commonTx.message());

        }

        return tx.finish(
            commonTx.previousBalance() < value ? EconomyResultType.NOT_ENOUGH_FUNDS : EconomyResultType.FAILED,
            commonTx.message()
        );
    }

    @Override
    public @NotNull EconomyTransaction deposit(BigDecimal amount) {
        var tx = TransactionFactory.transaction(this, amount, EconomyTransactionType.DEPOSIT);

        long value = CurrencyUtils.fromBigDecimal(currency.wrapping(), amount);

//        try {
//            value = amount.longValueExact();
//        } catch (ArithmeticException arith) {
//            // todo: use translations.
//            return tx.invalid(Text.literal("Common Bridge: couldn't convert amount to long"));
//        }

        var commonTx = account.increaseBalance(value);

        if (commonTx.isSuccessful()) {
            return tx.success(commonTx.message());
        } else {
            return tx.failed(commonTx.message());
        }
    }

    @Override
    public @NotNull EconomyTransferTransaction transfer(Account to, BigDecimal amount) {
        var tx = TransactionFactory.transfer(this, to, amount);

        long value = CurrencyUtils.fromBigDecimal(currency.wrapping(), amount);

//        try {
//            value = amount.longValueExact();
//        } catch (ArithmeticException arith) {
//            // todo: use translations.
//            return tx.invalid(Text.literal("Common Bridge: couldn't convert amount to long"));
//        }

        EconomyAccount toCommon = ((CommonImpactorAccount) to).account;

        var tryWithdraw = account.canDecreaseBalance(value);

        if (!tryWithdraw.isSuccessful()) {
            return tx.finish(
                tryWithdraw.previousBalance() < value ? EconomyResultType.NOT_ENOUGH_FUNDS : EconomyResultType.FAILED,
                tryWithdraw.message()
            );
        }

        var tryDeposit = toCommon.canIncreaseBalance(value);

        if (!tryDeposit.isSuccessful()) {
            return tx.failed(tryDeposit.message());
        }

        account.decreaseBalance(value);
        toCommon.increaseBalance(value);

        return tx.success(null);
    }

    @Override
    public @NotNull EconomyTransaction reset() {
        account.setBalance(0);

        return TransactionFactory.transaction(this, BigDecimal.ZERO, EconomyTransactionType.RESET)
            .success(null);
    }
}
