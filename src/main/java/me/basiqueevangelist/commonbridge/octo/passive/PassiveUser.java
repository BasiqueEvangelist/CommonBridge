package me.basiqueevangelist.commonbridge.octo.passive;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.transaction.Transaction;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.commonbridge.CommonBridge;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class PassiveUser implements User {
    protected final PassiveEconomy economy;

    public PassiveUser(PassiveEconomy economy) {
        this.economy = economy;
    }

    @Override
    public abstract Text getDisplayName();

    @Override
    public double getBalance(Currency currency) {
        //noinspection DataFlowIssue
        return currency.balanceProvider().getBalance(this);
    }

    @Override
    public Map<Currency, Double> getAllBalances() {
        Map<Currency, Double> results = new HashMap<>();

        for (var currency : economy.getCurrencies()) {
            //noinspection DataFlowIssue
            results.put(currency, currency.balanceProvider().getBalance(this));
        }

        return results;
    }

    @Override
    public boolean hasAmount(Currency currency, double amount) {
        return getBalance(currency) >= amount;
    }

    @Override
    public Transaction resetBalance(Currency currency) {
        //noinspection DataFlowIssue
        return currency.balanceProvider().setBalance(this, 0, currency);
    }

    @Override
    public Map<Currency, Transaction> resetAllBalances() {
        Map<Currency, Transaction> results = new HashMap<>();

        for (var currency : economy.getCurrencies()) {
            //noinspection DataFlowIssue
            results.put(currency, currency.balanceProvider().setBalance(this, 0, currency));
        }

        return results;
    }

    @Override
    public Transaction setBalance(Currency currency, double amount) {
        //noinspection DataFlowIssue
        return currency.balanceProvider().setBalance(this, amount, currency);
    }

    @Override
    public Transaction sendTo(User user, Currency currency, double amount) {
        //noinspection DataFlowIssue
        return currency.balanceProvider().sendTo(this, user, amount, currency);
    }

    @Override
    public Transaction depositMoney(Currency currency, double amount, String reason) {
        //noinspection DataFlowIssue
        return currency.balanceProvider().deposit(this, amount, reason, currency);
    }

    @Override
    public Transaction withdrawMoney(Currency currency, double amount, String reason) {
        //noinspection DataFlowIssue
        return currency.balanceProvider().withdraw(this, amount, reason, currency);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract String getIdentity();

    public static class Unique extends PassiveUser implements UniqueUser {
        private final GameProfile profile;

        public Unique(PassiveEconomy economy, UUID uuid) {
            super(economy);

            profile = Objects.requireNonNullElseGet(
                CommonBridge.SERVER.getUserCache().getByUuid(uuid).orElse(null),
                () -> new GameProfile(uuid, uuid.toString())
            );
        }

        @Override
        public Text getDisplayName() {
            return Text.literal(profile.getName());
        }

        @Override
        public String getIdentity() {
            return profile.getName();
        }

        @Override
        public UUID getUserID() {
            return profile.getId();
        }
    }

    public static class Fake extends PassiveUser implements FakeUser {
        private final Identifier id;

        public Fake(PassiveEconomy economy, Identifier id) {
            super(economy);

            this.id = id;
        }

        @Override
        public Identifier getResourceLocation() {
            return id;
        }

        @Override
        public Text getDisplayName() {
            return Text.literal(id.toString());
        }

        @Override
        public String getIdentity() {
            return id.toString();
        }
    }
}
