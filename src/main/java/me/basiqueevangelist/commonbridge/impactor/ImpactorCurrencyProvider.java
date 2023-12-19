package me.basiqueevangelist.commonbridge.impactor;

import eu.pb4.common.economy.api.CommonEconomy;
import me.basiqueevangelist.commonbridge.CommonBridge;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ImpactorCurrencyProvider implements CurrencyProvider {
    private final Set<Currency> currencies = new HashSet<>();
    private final Map<Identifier, Currency> currenciesByKey = new HashMap<>();
    private Currency primary;

    public ImpactorCurrencyProvider() {
        for (var currency : CommonEconomy.getCurrencies(CommonBridge.SERVER)) {
            var wrapped = new CommonImpactorCurrency(currency, currencies.isEmpty());

            if (wrapped.primary()) primary = wrapped;

            currencies.add(wrapped);
            currenciesByKey.put(currency.id(), wrapped);
        }
    }

    public void reload() {
        currencies.clear();
        currenciesByKey.clear();

        for (var currency : CommonEconomy.getCurrencies(CommonBridge.SERVER)) {
            var wrapped = new CommonImpactorCurrency(currency, currencies.isEmpty());

            if (wrapped.primary()) primary = wrapped;

            currencies.add(wrapped);
            currenciesByKey.put(currency.id(), wrapped);
        }
    }

    @Override
    public @NotNull Currency primary() {
        return primary;
    }

    @Override
    public Optional<Currency> currency(Key key) {
        return Optional.ofNullable(currenciesByKey.get(FabricAudiences.toNative(key)));
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
