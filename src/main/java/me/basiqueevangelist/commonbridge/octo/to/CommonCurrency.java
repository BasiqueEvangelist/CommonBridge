package me.basiqueevangelist.commonbridge.octo.to;

import com.epherical.octoecon.api.BalanceProvider;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.EconomyCurrency;
import me.basiqueevangelist.commonbridge.CommonBridge;
import me.basiqueevangelist.commonbridge.util.CurrencyUtils;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class CommonCurrency implements Currency {
    private final EconomyCurrency wrapping;
    private final Balance balance = new Balance();

    public CommonCurrency(EconomyCurrency wrapping) {
        this.wrapping = wrapping;
    }

    @Override
    public Text getCurrencySingularName() {
        return CurrencyUtils.nameSingular(wrapping);
    }

    @Override
    public Text getCurrencyPluralName() {
        return CurrencyUtils.namePlural(wrapping);
    }

    @Override
    public Text getCurrencySymbol() {
        return CurrencyUtils.symbol(wrapping);
    }

    @Override
    public int decimalPlaces() {
        return CurrencyUtils.decimalPlaces(wrapping);
    }

    @Override
    public Text format(double value) {
        return wrapping.formatValueText(toCommonValue(value), false);
    }

    @Override
    public Text format(double value, int decimalPlaces) {
        return wrapping.formatValueText(toCommonValue(value), false);
    }

    public long toCommonValue(double octo) {
        return CurrencyUtils.fromDouble(wrapping, octo);
    }

    public double toOctoValue(long common) {
        return CurrencyUtils.toDouble(wrapping, common);
    }

    @Override
    public @Nullable BalanceProvider balanceProvider() {
        return balance;
    }

    @Override
    public String getIdentity() {
        return wrapping.id().toString();
    }

    private class Balance implements BalanceProvider {
        @Override
        public double getBalance(User user) {
            var account = wrapping.provider().getDefaultAccount(CommonBridge.SERVER, profileFromUser(user), wrapping);

            if (account == null) return 0;

            return toOctoValue(account.balance());
        }

        @Override
        public Transaction setBalance(User user, double amount, Currency currencyUsed) {
            var tx = TransactionFactory.transaction(user, toOctoValue(toCommonValue(amount)), currencyUsed, Transaction.Type.SET);
            
            if (currencyUsed != CommonCurrency.this) return tx.fail("Cannot use unrelated currency with balance provider");

            long value = toCommonValue(amount);

//            if (value != amount) return tx.fail("Amount cannot be represented as a long");

            var account = wrapping.provider().getDefaultAccount(CommonBridge.SERVER, profileFromUser(user), wrapping);

            if (account == null) return tx.fail("User doesn't have a default account");

            account.setBalance(value);

            return tx.success("Set account balance to " + wrapping.formatValue(value, false));
        }

        @Override
        public Transaction sendTo(User from, User to, double amount, Currency currencyUsed) {
            var tx = TransactionFactory.transaction(from, -toOctoValue(toCommonValue(amount)), currencyUsed, Transaction.Type.DEPOSIT);

            if (currencyUsed != CommonCurrency.this) return tx.fail("Cannot use unrelated currency with balance provider");

            long value = toCommonValue(amount);

//            if (value != amount) return tx.fail("Amount cannot be represented as a long");

            var fromAccount = wrapping.provider().getDefaultAccount(CommonBridge.SERVER, profileFromUser(from), wrapping);
            var toAccount = wrapping.provider().getDefaultAccount(CommonBridge.SERVER, profileFromUser(to), wrapping);

            if (fromAccount == null) return tx.fail("Sender doesn't have a default account");
            if (toAccount == null) return tx.fail("Receiver doesn't have a default account");

            var tryWithdraw = fromAccount.canDecreaseBalance(value);

            if (tryWithdraw.isFailure()) return tx.fail(tryWithdraw.message().getString());

            var tryDeposit = toAccount.canIncreaseBalance(value);

            if (tryDeposit.isFailure()) return tx.fail(tryDeposit.message().getString());

            fromAccount.decreaseBalance(value);
            var res = toAccount.increaseBalance(value);

            return tx.success(res.message().getString());
        }

        @Override
        public Transaction deposit(User user, double amount, String reason, Currency currencyUsed) {
            var tx = TransactionFactory.transaction(user, toOctoValue(toCommonValue(amount)), currencyUsed, Transaction.Type.DEPOSIT);

            if (currencyUsed != CommonCurrency.this) return tx.fail("Cannot use unrelated currency with balance provider");

            long value = toCommonValue(amount);

//            if (value != amount) return tx.fail("Amount cannot be represented as a long");

            var account = wrapping.provider().getDefaultAccount(CommonBridge.SERVER, profileFromUser(user), wrapping);

            if (account == null) return tx.fail("User doesn't have a default account");

            var common = account.increaseBalance(value);

            return tx.finish(
                common.isSuccessful() ? Transaction.Response.SUCCESS : Transaction.Response.FAIL,
                common.message().getString()
            );
        }

        @Override
        public Transaction withdraw(User user, double amount, String reason, Currency currencyUsed) {
            var tx = TransactionFactory.transaction(user, toOctoValue(toCommonValue(amount)), currencyUsed, Transaction.Type.WITHDRAW);

            if (currencyUsed != CommonCurrency.this) return tx.fail("Cannot use unrelated currency with balance provider");

            long value = toCommonValue(amount);

//            if (value != amount) return tx.fail("Amount cannot be represented as a long");

            var account = wrapping.provider().getDefaultAccount(CommonBridge.SERVER, profileFromUser(user), wrapping);

            if (account == null) return tx.fail("User doesn't have a default account");

            var common = account.decreaseBalance(value);

            return tx.finish(
                common.isSuccessful() ? Transaction.Response.SUCCESS : Transaction.Response.FAIL,
                common.message().getString()
            );
        }

        private GameProfile profileFromUser(User user) {
            if (user instanceof UniqueUser unique) {
                return Objects.requireNonNullElseGet(
                    CommonBridge.SERVER.getUserCache().getByUuid(unique.getUserID()).orElse(null),
                    () -> new GameProfile(unique.getUserID(), unique.getIdentity())
                );
            } else {
                return new GameProfile(
                    UUID.nameUUIDFromBytes(user.getIdentity().getBytes(StandardCharsets.UTF_8)),
                    user.getIdentity()
                );
            }
        }
    }
}
