package me.basiqueevangelist.commonbridge.util;

import eu.pb4.common.economy.api.EconomyCurrency;
import net.minecraft.text.Text;

import java.math.BigDecimal;

public final class CurrencyUtils {
    private CurrencyUtils() {

    }

    public static Text nameSingular(EconomyCurrency currency) {
        if (currency instanceof ExtraEconomyCurrency extra)
            return extra.nameSingular();
        else
            return currency.name();
    }

    public static Text namePlural(EconomyCurrency currency) {
        if (currency instanceof ExtraEconomyCurrency extra)
            return extra.namePlural();
        else
            return currency.name();
    }

    public static Text symbol(EconomyCurrency currency) {
        if (currency instanceof ExtraEconomyCurrency extra)
            return extra.symbol();
        else
            return currency.name();
    }

    public static int decimalPlaces(EconomyCurrency currency) {
        if (currency instanceof ExtraEconomyCurrency extra)
            return extra.decimalPlaces();
        else
            return 0;
    }

    public static double toDouble(EconomyCurrency currency, long amount) {
        return (double)(amount) / Math.pow(10, decimalPlaces(currency));
    }

    public static long fromDouble(EconomyCurrency currency, double amount) {
        return (long)(amount * Math.pow(10, decimalPlaces(currency)));
    }

    public static BigDecimal toBigDecimal(EconomyCurrency currency, long amount) {
        return BigDecimal.valueOf(amount)
            .movePointLeft(decimalPlaces(currency));
    }

    public static long fromBigDecimal(EconomyCurrency currency, BigDecimal amount) {
        return amount
            .movePointRight(decimalPlaces(currency))
            .longValue();
    }
}
