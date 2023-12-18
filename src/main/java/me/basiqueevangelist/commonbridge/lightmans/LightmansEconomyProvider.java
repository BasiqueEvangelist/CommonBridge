package me.basiqueevangelist.commonbridge.lightmans;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import io.github.lightman314.lightmanscurrency.common.core.ModItems;
import io.github.lightman314.lightmanscurrency.common.money.bank.BankAccount;
import io.github.lightman314.lightmanscurrency.common.money.bank.BankSaveData;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class LightmansEconomyProvider implements EconomyProvider {
    public static final LightmansEconomyProvider INSTANCE = new LightmansEconomyProvider();

    public static void init() {
        CommonEconomy.register("lightmanscurrency", INSTANCE);
    }

    @Override
    public Text name() {
        return Text.literal("Lightman's Currency");
    }

    @Override
    public @Nullable EconomyAccount getAccount(MinecraftServer server, GameProfile profile, String accountId) {
        if (!accountId.equals(LightmansBankAccount.ID.getPath())) return null;

        BankAccount account = BankSaveData.GetBankAccount(false, profile.getId());

        if (account == null) return null;

        return new LightmansBankAccount(profile.getId(), server, account);
    }

    @Override
    public Collection<EconomyAccount> getAccounts(MinecraftServer server, GameProfile profile) {
        BankAccount account = BankSaveData.GetBankAccount(false, profile.getId());

        if (account == null) return Collections.emptySet();

        return Collections.singleton(new LightmansBankAccount(profile.getId(), server, account));
    }

    @Override
    public @Nullable EconomyCurrency getCurrency(MinecraftServer server, String currencyId) {
        if (!currencyId.equals(CoinsCurrency.ID.getPath())) return null;

        return CoinsCurrency.INSTANCE;
    }

    @Override
    public Collection<EconomyCurrency> getCurrencies(MinecraftServer server) {
        return Collections.singleton(CoinsCurrency.INSTANCE);
    }

    @Override
    public @Nullable String defaultAccount(MinecraftServer server, GameProfile profile, EconomyCurrency currency) {
        return LightmansBankAccount.ID.getPath();
    }

    @Override
    public ItemStack icon() {
        return ModItems.TRADING_CORE.getDefaultStack();
    }
}
