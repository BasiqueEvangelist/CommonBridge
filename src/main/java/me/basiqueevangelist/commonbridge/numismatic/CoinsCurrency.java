package me.basiqueevangelist.commonbridge.numismatic;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.currency.Currency;
import com.glisco.numismaticoverhaul.currency.CurrencyConverter;
import com.glisco.numismaticoverhaul.item.CoinItem;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class CoinsCurrency implements EconomyCurrency {
    public static final CoinsCurrency INSTANCE = new CoinsCurrency();
    public static final Identifier ID = NumismaticOverhaul.id("coins");

    @Override
    public Text name() {
        return Text.literal("Coins");
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public Text formatValueText(long value, boolean precise) {
        if (precise) return Text.literal("¢" + value);

        MutableText text = Text.literal("");

        text.append(Text.literal("[")
            .formatted(Formatting.GRAY));

        if (value == 0) {
            // No money?
            text.append(Text.literal("0 ")
                .formatted(Formatting.AQUA));
            text.append(Text.translatable("currency.numismatic-overhaul." + Currency.BRONZE.name().toLowerCase())
                .formatted(Formatting.WHITE));
        } else {
            int i = 0;
            for (ItemStack stack : CurrencyConverter.getAsItemStackList(value)) {
                if (i > 0) text.append(", ");

                text
                    .append(Text.literal(Integer.toString(stack.getCount()))
                        .formatted(Formatting.AQUA))
                    .append(" ");
                text.append(Text.translatable("currency.numismatic-overhaul." + ((CoinItem) stack.getItem()).currency.name().toLowerCase())
                    .formatted(Formatting.WHITE));
                i++;
            }
        }

        text.append(Text.literal("]")
            .formatted(Formatting.GRAY));

        return text;
    }

    @Override
    public String formatValue(long value, boolean precise) {
        if (precise) return "¢" + value;

        if (value == 0) {
        }

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        if (value == 0) {
            // No money?
            sb.append("0 ");
            sb.append(I18n.translate("currency.numismatic-overhaul." + Currency.BRONZE.name().toLowerCase()));
        } else {
            int i = 0;
            for (ItemStack stack : CurrencyConverter.getAsItemStackList(value)) {
                if (i > 0) sb.append(", ");

                sb.append(stack.getCount()).append(" ");
                sb.append(I18n.translate("currency.numismatic-overhaul." + ((CoinItem) stack.getItem()).currency.name().toLowerCase()));
                i++;
            }
        }

        sb.append("]");

        return sb.toString();
    }

    @Override
    public long parseValue(String value) throws NumberFormatException {
        if (value.startsWith("¢"))
            value = value.substring(1);

        if (value.isEmpty())
            return 0;

        return Long.parseLong(value);
    }

    @Override
    public EconomyProvider provider() {
        return NumismaticEconomyProvider.INSTANCE;
    }

    @Override
    public ItemStack icon() {
        return new ItemStack(NumismaticOverhaulItems.GOLD_COIN);
    }
}
