package me.basiqueevangelist.commonbridge.octo.from;

import com.epherical.octoecon.api.Currency;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import me.basiqueevangelist.commonbridge.util.CurrencyUtils;
import me.basiqueevangelist.commonbridge.util.ExtraEconomyCurrency;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OctoEconomyCurrency implements EconomyCurrency, ExtraEconomyCurrency {
    private final Currency wrapped;
    private final Identifier id;

    public OctoEconomyCurrency(Currency wrapped) {
        this.wrapped = wrapped;

        var wrappedId = new Identifier(wrapped.getIdentity());
        this.id = new Identifier("octo-economy-api", wrappedId.getNamespace() + "/" + wrappedId.getPath());
    }

    @Override
    public Text name() {
        return wrapped.getCurrencyPluralName();
    }

    @Override
    public Text nameSingular() {
        return wrapped.getCurrencySingularName();
    }

    @Override
    public Text namePlural() {
        return wrapped.getCurrencyPluralName();
    }

    @Override
    public Text symbol() {
        return wrapped.getCurrencySymbol();
    }

    @Override
    public int decimalPlaces() {
        return wrapped.decimalPlaces();
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Text formatValueText(long value, boolean precise) {
        if (!precise)
            return wrapped.format(toOctoValue(value));

        return Text.literal(Double.toString(toOctoValue(value)));
    }

    @Override
    public String formatValue(long value, boolean precise) {
        if (!precise)
            return wrapped.format(toOctoValue(value)).getString();

        return Double.toString(toOctoValue(value));
    }

    @Override
    public long parseValue(String value) throws NumberFormatException {
        if (value.isEmpty()) return 0;

        return fromOctoValue(Double.parseDouble(value));
    }

    public double toOctoValue(long common) {
        return CurrencyUtils.toDouble(this, common);
    }

    public long fromOctoValue(double octo) {
        return CurrencyUtils.fromDouble(this, octo);
    }

    @Override
    public EconomyProvider provider() {
        return OctoEconomyProvider.INSTANCE;
    }

    public Currency wrapped() {
        return wrapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OctoEconomyCurrency that = (OctoEconomyCurrency) o;

        return wrapped.equals(that.wrapped);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public ItemStack icon() {
        return Items.INK_SAC.getDefaultStack();
    }
}
