package me.basiqueevangelist.commonbridge.impactor;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.Supplier;

public interface TransactionFactory<T> {
    static TransactionFactory<BridgeTransaction> transaction(Account account, BigDecimal amount, EconomyTransactionType type) {
        return (result, text) -> new BridgeTransaction(account, amount, type, result, text);
    }

    static TransactionFactory<BridgeTransferTransaction> transfer(Account from, Account to, BigDecimal amount) {
        return (result, text) -> new BridgeTransferTransaction(from, to, amount, result, text);
    }

    T finish(EconomyResultType result, @Nullable Text text);

    default T success(@Nullable Text text) {
        return finish(EconomyResultType.SUCCESS, text);
    }

    default T failed(@Nullable Text text) {
        return finish(EconomyResultType.FAILED, text);
    }

    default T invalid(@Nullable Text text) {
        return finish(EconomyResultType.INVALID, text);
    }

    record BridgeTransaction(
        Account account,
        BigDecimal amount,
        EconomyTransactionType type,
        EconomyResultType result,
        @Nullable Text messageText
    ) implements EconomyTransaction {
        @Override
        public Currency currency() {
            return account.currency();
        }

        @Override
        public @Nullable Supplier<Component> message() {
            if (messageText == null) return null;

            return messageText::asComponent;
        }
    }

    record BridgeTransferTransaction(
        Account from,
        Account to,
        BigDecimal amount,
        EconomyResultType result,
        Text messageText
    ) implements EconomyTransferTransaction {
        @Override
        public Currency currency() {
            return from.currency();
        }

        @Override
        public @Nullable Supplier<Component> message() {
            if (messageText == null) return null;

            return messageText::asComponent;
        }
    }
}
