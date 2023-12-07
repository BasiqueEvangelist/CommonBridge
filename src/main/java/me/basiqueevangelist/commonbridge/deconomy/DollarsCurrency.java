package me.basiqueevangelist.commonbridge.deconomy;

import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DollarsCurrency implements EconomyCurrency {
    public static final DollarsCurrency INSTANCE = new DollarsCurrency();
    public static final Identifier ID = new Identifier("diamondeconomy", "dollars");

    @Override
    public Text name() {
        return Text.literal("Dollars");
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public String formatValue(long value, boolean precise) {
        return "$" + value;
    }

    @Override
    public long parseValue(String value) throws NumberFormatException {
        if (value.startsWith("$"))
            value = value.substring(1);

        if (value.isEmpty())
            return 0;

        return Long.parseLong(value);
    }

    @Override
    public EconomyProvider provider() {
        return DiamondEconomyProvider.INSTANCE;
    }

    @Override
    public ItemStack icon() {
        // TODO: use actual currency in DiamondEconomyConfig
        return Items.DIAMOND.getDefaultStack();
    }
}
