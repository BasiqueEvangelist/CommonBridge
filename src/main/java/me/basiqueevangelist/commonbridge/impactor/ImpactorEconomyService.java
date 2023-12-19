package me.basiqueevangelist.commonbridge.impactor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.CommonEconomy;
import me.basiqueevangelist.commonbridge.CommonBridge;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.economy.events.SuggestEconomyServiceEvent;
import net.impactdev.impactor.api.events.ImpactorEventBus;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ImpactorEconomyService implements EconomyService {
    private static final PluginMetadata METADATA = PluginMetadata.builder()
        .id("common-bridge-impactor")
        .name("Common Bridge: Impactor Integration")
        .version(CommonBridge.MOD_CONTAINER.getMetadata().getVersion().getFriendlyString())
        .build();

    private final ImpactorCurrencyProvider currencies = new ImpactorCurrencyProvider();

    public static void init() {
        ImpactorEventBus.bus().subscribe(SuggestEconomyServiceEvent.class, event -> {
            if (CommonEconomy.providers().isEmpty()) return;

            event.suggest(METADATA, ImpactorEconomyService::new, 1);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var service = Impactor.instance().services().provide(EconomyService.class);

            if (service instanceof ImpactorEconomyService ies) {
                // Just to make sure Octo Economy API or whatever works.
                ies.currencies.reload();
            }
        });
    }

    @Override
    public CurrencyProvider currencies() {
        return currencies;
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(Currency currency, UUID uuid) {
        var common = ((CommonImpactorCurrency) currency).wrapping();
        GameProfile profile = fromUUID(uuid);
        String accountPath = common.provider().defaultAccount(CommonBridge.SERVER, profile, common);

        if (accountPath == null)
            return CompletableFuture.completedFuture(false);

        return CompletableFuture.completedFuture(
            common.provider().getAccount(CommonBridge.SERVER, profile, accountPath) != null);
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid) {
        var common = ((CommonImpactorCurrency) currency).wrapping();
        var account = common.provider().getDefaultAccount(CommonBridge.SERVER, fromUUID(uuid), common);

        if (account == null)
            return CompletableFuture.completedFuture(null);
        else
            return CompletableFuture.completedFuture(new CommonImpactorAccount(account, (CommonImpactorCurrency) currency));
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid, Account.AccountModifier modifier) {
        return account(currency, uuid);
    }

    @Override
    public CompletableFuture<Multimap<Currency, Account>> accounts() {
        return CompletableFuture.completedFuture(ImmutableMultimap.of());
    }

    @Override
    public CompletableFuture<Void> deleteAccount(Currency currency, UUID uuid) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String name() {
        return "Common Economy API";
    }

    private GameProfile fromUUID(UUID playerId) {
        return Objects.requireNonNullElseGet(
            CommonBridge.SERVER.getUserCache().getByUuid(playerId).orElse(null),
            () -> new GameProfile(playerId, playerId.toString())
        );
    }
}
