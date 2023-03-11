package me.basiqueevangelist.commonbridge.numismatic;

import com.glisco.numismaticoverhaul.item.MoneyBagItem;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import io.wispforest.owo.offline.OfflineDataLookup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class NumismaticEconomyProvider implements EconomyProvider {
    public static final NumismaticEconomyProvider INSTANCE = new NumismaticEconomyProvider();

    public static void init() {
        CommonEconomy.register("numismatic-overhaul", INSTANCE);
    }

    @Override
    public Text name() {
        return Text.literal("Numismatic Overhaul");
    }

    @Override
    public @Nullable EconomyAccount getAccount(MinecraftServer server, GameProfile profile, String accountId) {
        if (!accountId.equals("purse")) return null;

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(profile.getId());

        if (player != null)
            return new OnlinePurseAccount(player);

        if (OfflineDataLookup.get(profile.getId()) != null)
            return new OfflinePurseAccount(profile.getId(), server);

        return null;
    }

    @Override
    public Collection<EconomyAccount> getAccounts(MinecraftServer server, GameProfile profile) {
        var acc = getAccount(server, profile, "purse");

        if (acc != null) return Collections.singleton(acc);
        else return Collections.emptySet();
    }

    @Override
    public @Nullable EconomyCurrency getCurrency(MinecraftServer server, String currencyId) {
        return currencyId.equals("coins") ? CoinsCurrency.INSTANCE : null;
    }

    @Override
    public Collection<EconomyCurrency> getCurrencies(MinecraftServer server) {
        return Collections.singleton(CoinsCurrency.INSTANCE);
    }

    @Override
    public @Nullable String defaultAccount(MinecraftServer server, GameProfile profile, EconomyCurrency currency) {
        return currency == CoinsCurrency.INSTANCE ? "purse" : null;
    }

    @Override
    public ItemStack icon() {
        return new ItemStack(NumismaticOverhaulItems.MONEY_BAG);
    }
}
