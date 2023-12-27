package me.basiqueevangelist.commonbridge.impactor;

import eu.pb4.common.economy.api.EconomyCurrency;
import me.basiqueevangelist.commonbridge.util.AdventureUtils;
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

    private final Key key;
    private final Component singularAdv;
    private final Component pluralAdv;
    private final Component symbolAdv;

    public CommonImpactorCurrency(EconomyCurrency wrapping, boolean primary) {
        this.wrapping = wrapping;
        this.primary = primary;

        this.key = AdventureUtils.toAdventure(wrapping.id());
        this.singularAdv = AdventureUtils.toAdventure(CurrencyUtils.nameSingular(wrapping));
        this.pluralAdv = AdventureUtils.toAdventure(CurrencyUtils.namePlural(wrapping));
        this.symbolAdv = AdventureUtils.toAdventure(CurrencyUtils.symbol(wrapping));
    }

    @Override
    public Key key() {
        return key;
    }

    @Override
    public Component singular() {
        return singularAdv;
    }

    @Override
    public Component plural() {
        return pluralAdv;
    }

    @Override
    public Component symbol() {
        return symbolAdv;
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

        return AdventureUtils.toAdventure(wrapping.formatValueText(value, condensed));
    }

    public EconomyCurrency wrapping() {
        return wrapping;
    }
}
