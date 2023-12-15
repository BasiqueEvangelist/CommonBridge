package me.basiqueevangelist.commonbridge.impactor;

import eu.pb4.common.economy.api.EconomyCurrency;
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
        // TODO: fix names
        return wrapping.name().asComponent();
    }

    @Override
    public Component plural() {
        // TODO: fix names
        return wrapping.name().asComponent();
    }

    @Override
    public Component symbol() {
        // TODO: fix names
        return wrapping.name().asComponent();
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
        return 0;
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
        // guys converting from big decimal to long won't definitely lose information right
        long value = amount.longValue();

        return wrapping.formatValueText(value, condensed).asComponent();
    }

    public EconomyCurrency wrapping() {
        return wrapping;
    }
}
