package me.basiqueevangelist.commonbridge.lightmans;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import eu.pb4.common.economy.api.EconomyTransaction;
import io.github.lightman314.lightmanscurrency.common.core.ModItems;
import io.github.lightman314.lightmanscurrency.common.money.CoinValue;
import io.github.lightman314.lightmanscurrency.common.money.bank.BankAccount;
import io.github.lightman314.lightmanscurrency.common.money.bank.BankSaveData;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class LightmansBankAccount implements EconomyAccount {
    public static final Identifier ID = new Identifier("lightmanscurrency", "bank_account");

    private final UUID playerId;
    private final MinecraftServer server;
    private final BankAccount account;

    public LightmansBankAccount(UUID playerId, MinecraftServer server) {
        this.playerId = playerId;
        this.server = server;

        this.account = BankSaveData.GetBankAccount(false, playerId);
    }

    @Override
    public Text name() {
        return Text.translatable("lightmanscurrency.bankaccount", playerName());
    }

    private String playerName() {
        ServerPlayerEntity online = server.getPlayerManager().getPlayer(playerId);

        if (online != null) return online.getEntityName();

        return server.getUserCache().getByUuid(playerId).map(GameProfile::getName).orElseGet(playerId::toString);
    }

    @Override
    public UUID owner() {
        return playerId;
    }

    @Override
    public Identifier id() {
        return ID;
    }


    @Override
    public long balance() {
        return account.getCoinStorage().getRawValue();
    }

    @Override
    public EconomyTransaction canIncreaseBalance(long value) {
        long current = balance();

        return new EconomyTransaction.Simple(
            true,
            Text.literal("Added " + CoinsCurrency.INSTANCE.formatValue(value, false) + " to " + playerName() + "'s account"),
            current + value,
            current,
            value,
            this
        );
    }

    @Override
    public EconomyTransaction canDecreaseBalance(long value) {
        long current = balance();

        if (current < value) {
            return new EconomyTransaction.Simple(
                false,
                Text.literal(playerName() + " doesn't have enough coins! (" + CoinsCurrency.INSTANCE.formatValue(current, false) + " < " + CoinsCurrency.INSTANCE.formatValue(value, false) + ")"),
                current,
                current,
                0,
                this
            );
        } else {
            return new EconomyTransaction.Simple(
                true,
                Text.literal("Removed " + CoinsCurrency.INSTANCE.formatValue(value, false) + " from " + playerName() + "'s account"),
                current - value,
                current,
                value,
                this
            );
        }
    }

    @Override
    public void setBalance(long value) {
        long current = balance();

        if (current > value) {
            account.withdrawCoins(new CoinValue(current - value));
        } else if (current < value) {
            account.depositCoins(new CoinValue(value - current));
        }
    }

    @Override
    public EconomyProvider provider() {
        return LightmansEconomyProvider.INSTANCE;
    }

    @Override
    public EconomyCurrency currency() {
        return CoinsCurrency.INSTANCE;
    }

    @Override
    public ItemStack accountIcon() {
        return ModItems.PORTABLE_ATM.getDefaultStack();
    }
}
