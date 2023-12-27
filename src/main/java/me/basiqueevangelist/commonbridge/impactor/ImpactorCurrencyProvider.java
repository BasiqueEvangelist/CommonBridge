package me.basiqueevangelist.commonbridge.impactor;

import eu.pb4.common.economy.api.CommonEconomy;
import me.basiqueevangelist.commonbridge.CommonBridge;
import me.basiqueevangelist.commonbridge.util.AdventureUtils;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.kyori.adventure.key.Key;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ImpactorCurrencyProvider implements CurrencyProvider {
    private final Set<Currency> currencies = new HashSet<>();
    private final Map<Key, Currency> currenciesByKey = new HashMap<>();
    private Currency primary;

    public ImpactorCurrencyProvider() {
        reload();
    }

    public void reload() {
        currencies.clear();
        currenciesByKey.clear();

        for (var currency : CommonEconomy.getCurrencies(CommonBridge.SERVER)) {
            var wrapped = new CommonImpactorCurrency(currency, currencies.isEmpty());

            if (wrapped.primary()) primary = wrapped;

            currencies.add(wrapped);
            currenciesByKey.put(AdventureUtils.toAdventure(currency.id()), wrapped);
        }
    }

    @Override
    public @NotNull Currency primary() {
        return primary;
    }

    @Override
    public Optional<Currency> currency(Key key) {
        return Optional.ofNullable(currenciesByKey.get(key));
    }

    @Override
    public Set<Currency> registered() {
        return currencies;
    }

    @Override
    public CompletableFuture<Boolean> register(Currency currency) {
        return CompletableFuture.completedFuture(false);
    }
}
