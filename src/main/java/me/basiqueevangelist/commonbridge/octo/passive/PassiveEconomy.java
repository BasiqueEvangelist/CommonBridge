package me.basiqueevangelist.commonbridge.octo.passive;

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.basiqueevangelist.commonbridge.CommonBridge;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PassiveEconomy implements Economy {
    private final List<Currency> currencies;
    private final Map<Identifier, Currency> currencyById = new HashMap<>();
    private final LoadingCache<UUID, UniqueUser> uniqueUserCache = CacheBuilder.newBuilder()
        .softValues()
        .maximumSize(250)
        .build(CacheLoader.from(id -> new PassiveUser.Unique(this, id)));
    private final LoadingCache<Identifier, FakeUser> fakeUserCache = CacheBuilder.newBuilder()
        .softValues()
        .maximumSize(250)
        .build(CacheLoader.from(id -> new PassiveUser.Fake(this, id)));

    public PassiveEconomy() {
        currencies = EconomyEvents.CURRENCY_ADD_EVENT.invoker().addCurrency();

        for (var currency : currencies) {
            if (currency.balanceProvider() == null)
                throw new UnsupportedOperationException("Currency " + currency.getIdentity() + " doesn't have a BalanceProvider");

            currencyById.put(new Identifier(currency.getIdentity()), currency);
        }
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return currencies;
    }

    @Override
    public Currency getDefaultCurrency() {
        return currencies.get(0);
    }

    @Override
    public @Nullable Currency getCurrency(Identifier identifier) {
        return currencyById.get(identifier);
    }

    @Override
    public @Nullable FakeUser getOrCreateAccount(Identifier identifier) {
        return fakeUserCache.getUnchecked(identifier);
    }

    @Override
    public @Nullable UniqueUser getOrCreatePlayerAccount(UUID identifier) {
        return uniqueUserCache.getUnchecked(identifier);
    }

    @Override
    public @Nullable UniqueUser getPlayerAccountByName(String name) {
        //noinspection DataFlowIssue
        var profile = CommonBridge.SERVER.getUserCache().findByName(name).orElse(null);

        if (profile == null) return null;

        return uniqueUserCache.getUnchecked(profile.getId());
    }

    @Override
    public Collection<UniqueUser> getUniqueUsers() {
        // We don't store any state, so...
        return Collections.emptyList();
    }

    @Override
    public Collection<User> getAllUsers() {
        // We don't store any state, so...
        return Collections.emptyList();
    }

    @Override
    public Collection<FakeUser> getFakeUsers() {
        // We don't store any state, so...
        return Collections.emptyList();
    }

    @Override
    public boolean hasAccount(UUID identifier) {
        return true;
    }

    @Override
    public boolean hasAccount(Identifier identifier) {
        return true;
    }

    @Override
    public boolean deleteAccount(UUID identifier) {
        return false;
    }

    @Override
    public boolean deleteAccount(Identifier identifier) {
        return false;
    }

}
