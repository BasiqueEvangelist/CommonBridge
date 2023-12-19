package me.basiqueevangelist.commonbridge.octo.from;

import com.epherical.octoecon.OctoEconomy;
import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import me.basiqueevangelist.commonbridge.octo.to.CommonCurrency;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OctoEconomyProvider implements EconomyProvider {
    public static final OctoEconomyProvider INSTANCE = new OctoEconomyProvider();
    private final Map<Currency, OctoEconomyCurrency> currencies = new HashMap<>();

    public static void init() {
        CommonEconomy.register("octo-economy-api", INSTANCE);

        EconomyEvents.ECONOMY_CHANGE_EVENT.register(unused -> INSTANCE.currencies.clear());
    }

    @Override
    public Text name() {
        return Text.literal("Octo Economy API");
    }

    @Override
    public @Nullable EconomyAccount getAccount(MinecraftServer server, GameProfile profile, String accountId) {
        Economy economy = OctoEconomy.getInstance().getCurrentEconomy();
        if (economy == null) return null;

        var user = economy.getOrCreatePlayerAccount(profile.getId());
        if (user == null) return null;

        var currency = getCurrency(server, accountId);
        if (currency == null) return null;

        return new OctoEconomyAccount(user, currency);
    }

    @Override
    public Collection<EconomyAccount> getAccounts(MinecraftServer server, GameProfile profile) {
        Economy economy = OctoEconomy.getInstance().getCurrentEconomy();
        if (economy == null) return Collections.emptyList();

        var user = economy.getOrCreatePlayerAccount(profile.getId());
        if (user == null) return Collections.emptyList();

        Collection<Currency> octo = economy.getCurrencies();
        List<EconomyAccount> common = new ArrayList<>(octo.size());

        for (Currency currency : octo) {
            if (currency instanceof CommonCurrency) continue;

            common.add(new OctoEconomyAccount(user, toCommon(currency)));
        }

        return common;
    }

    @Override
    public @Nullable OctoEconomyCurrency getCurrency(MinecraftServer server, String currencyId) {
        Economy economy = OctoEconomy.getInstance().getCurrentEconomy();
        if (economy == null) return null;

        int slashIdx = currencyId.indexOf('/');
        if (slashIdx == -1) return null;

        Currency octo = economy.getCurrency(new Identifier(currencyId.substring(0, slashIdx), currencyId.substring(slashIdx + 1)));
        if (octo == null) return null;
        if (octo instanceof CommonCurrency) return null;

        return toCommon(octo);
    }

    @Override
    public Collection<EconomyCurrency> getCurrencies(MinecraftServer server) {
        Economy economy = OctoEconomy.getInstance().getCurrentEconomy();
        if (economy == null) return Collections.emptyList();

        Collection<Currency> octo = economy.getCurrencies();
        List<EconomyCurrency> common = new ArrayList<>(octo.size());

        for (Currency currency : octo) {
            if (currency instanceof CommonCurrency) continue;

            common.add(toCommon(currency));
        }

        return common;
    }

    @Override
    public @Nullable String defaultAccount(MinecraftServer server, GameProfile profile, EconomyCurrency currency) {
        return currency.id().getPath();
    }

    private OctoEconomyCurrency toCommon(Currency currency) {
        return currencies.computeIfAbsent(currency, OctoEconomyCurrency::new);
    }

    @Override
    public ItemStack icon() {
        return Items.INK_SAC.getDefaultStack();
    }
}
