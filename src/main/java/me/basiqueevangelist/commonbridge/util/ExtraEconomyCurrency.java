package me.basiqueevangelist.commonbridge.util;

import eu.pb4.common.economy.api.EconomyCurrency;
import net.minecraft.text.Text;

public interface ExtraEconomyCurrency extends EconomyCurrency {
    default Text nameSingular() {
        return name();
    }

    default Text namePlural() {
        return name();
    }

    default Text symbol() {
        return name();
    }

    default int decimalPlaces() {
        return 0;
    }
}
