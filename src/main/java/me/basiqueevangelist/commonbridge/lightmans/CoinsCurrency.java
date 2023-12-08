package me.basiqueevangelist.commonbridge.lightmans;

import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import io.github.lightman314.lightmanscurrency.common.core.ModBlocks;
import io.github.lightman314.lightmanscurrency.common.money.CoinValue;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CoinsCurrency implements EconomyCurrency {
    public static final CoinsCurrency INSTANCE = new CoinsCurrency();
    public static final Identifier ID = new Identifier("lightmanscurrency", "coins");

    @Override
    public Text name() {
        // TODO: make this an actual translation.
        return Text.literal("Coins");
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public String formatValue(long value, boolean precise) {
        // TODO: make this better
        if (precise) return String.valueOf(value);

        if (value <= 0) return "0";

        return new CoinValue(value).getString();
    }

    @Override
    public long parseValue(String value) throws NumberFormatException {
        // TODO: same as formatValue

        if (value.isEmpty()) return 0;

        return Long.parseLong(value);
    }

    @Override
    public EconomyProvider provider() {
        return LightmansEconomyProvider.INSTANCE;
    }

    @Override
    public ItemStack icon() {
        ItemStack stack = new ItemStack(ModBlocks.COINPILE_GOLD.item);
        stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        return stack;
    }
}
