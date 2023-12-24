package me.basiqueevangelist.commonbridge.impactor;

import eu.pb4.common.economy.api.EconomyCurrency;
import me.basiqueevangelist.commonbridge.util.CurrencyUtils;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Locale;

public class CommonImpactorCurrency implements Currency {
    private final EconomyCurrency wrapping;
    private final boolean primary;

    public CommonImpactorCurrency(EconomyCurrency wrapping, boolean primary) {
        this.wrapping = wrapping;
        this.primary = primary;
    }

    @Override
    public Key key() {
        return wrapping.id();
    }

    @Override
    public Component singular() {
        return CurrencyUtils.nameSingular(wrapping).asComponent();
    }

    @Override
    public Component plural() {
        return CurrencyUtils.namePlural(wrapping).asComponent();
    }

    @Override
    public Component symbol() {
        return CurrencyUtils.symbol(wrapping).asComponent();
    }

    @Override
    public SymbolFormatting formatting() {
        return SymbolFormatting.AFTER;
    }

    @Override
    public BigDecimal defaultAccountBalance() {
        return BigDecimal.ZERO;
    }

    @Override
    public int decimals() {
        return CurrencyUtils.decimalPlaces(wrapping);
    }

    @Override
    public boolean primary() {
        return primary;
    }

    @Override
    public TriState transferable() {
        return TriState.TRUE;
    }

    @Override
    public Component format(@NotNull BigDecimal amount, boolean condensed, @NotNull Locale locale) {
        long value = CurrencyUtils.fromBigDecimal(wrapping, amount);

        return wrapping.formatValueText(value, condensed).asComponent();
    }

    public EconomyCurrency wrapping() {
        return wrapping;
    }
}
