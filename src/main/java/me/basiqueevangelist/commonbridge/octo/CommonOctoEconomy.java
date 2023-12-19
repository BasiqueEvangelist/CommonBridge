package me.basiqueevangelist.commonbridge.octo;

import com.epherical.octoecon.OctoEconomy;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.event.EconomyEvents;
import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.common.economy.api.EconomyCurrency;
import me.basiqueevangelist.commonbridge.CommonBridge;
import me.basiqueevangelist.commonbridge.octo.from.OctoEconomyProvider;
import me.basiqueevangelist.commonbridge.octo.passive.PassiveEconomy;
import me.basiqueevangelist.commonbridge.octo.to.CommonCurrency;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommonOctoEconomy {
    public static final Identifier PASSIVE_ECONOMY = new Identifier("common-bridge", "passive_economy");

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.addPhaseOrdering(Event.DEFAULT_PHASE, PASSIVE_ECONOMY);
        ServerLifecycleEvents.SERVER_STARTING.register(PASSIVE_ECONOMY, server -> {
            if (OctoEconomy.getInstance().getCurrentEconomy() != null) return;

            EconomyEvents.ECONOMY_CHANGE_EVENT.invoker().onEconomyChanged(new PassiveEconomy());
        });

        EconomyEvents.CURRENCY_ADD_EVENT.register(() -> {
            Collection<EconomyCurrency> commonCurrencies = CommonEconomy.getCurrencies(CommonBridge.SERVER);
            List<Currency> currencies = new ArrayList<>(commonCurrencies.size());

            for (var common : commonCurrencies) {
                currencies.add(new CommonCurrency(common));
            }

            return currencies;
        });

        OctoEconomyProvider.init();
    }
}
