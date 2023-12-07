package me.basiqueevangelist.commonbridge.deconomy;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class DiamondEconomyProvider implements EconomyProvider {
    public static final DiamondEconomyProvider INSTANCE = new DiamondEconomyProvider();

    public static void init() {
        CommonEconomy.register("diamondeconomy", INSTANCE);
    }

    @Override
    public Text name() {
        return Text.literal("Diamond Economy");
    }

    @Override
    public @Nullable EconomyAccount getAccount(MinecraftServer server, GameProfile profile, String accountId) {
        if (!accountId.equals(DiamondAccount.ID.getPath())) return null;

        return new DiamondAccount(profile.getId());
    }

    @Override
    public Collection<EconomyAccount> getAccounts(MinecraftServer server, GameProfile profile) {
        return Collections.singleton(new DiamondAccount(profile.getId()));
    }

    @Override
    public @Nullable EconomyCurrency getCurrency(MinecraftServer server, String currencyId) {
        if (!currencyId.equals(DollarsCurrency.ID.getPath())) return null;

        return DollarsCurrency.INSTANCE;
    }

    @Override
    public Collection<EconomyCurrency> getCurrencies(MinecraftServer server) {
        return Collections.singleton(DollarsCurrency.INSTANCE);
    }

    @Override
    public @Nullable String defaultAccount(MinecraftServer server, GameProfile profile, EconomyCurrency currency) {
        return "account";
    }

    @Override
    public ItemStack icon() {
        return Items.DIAMOND.getDefaultStack();
    }
}
