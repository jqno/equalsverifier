package nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;

import java.math.BigDecimal;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import org.javamoney.moneta.Money;

public final class JavaMoneyFactoryProvider implements FactoryProvider {
    private static final CurrencyUnit BRAZILIAN_REAL = Monetary.getCurrency("BRL");
    private static final CurrencyUnit US_DOLLAR = Monetary.getCurrency("USD");
    private static final CurrencyUnit EURO = Monetary.getCurrency("EUR");

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        final BigDecimal ten = new BigDecimal(10);
        final BigDecimal fiveHundred = new BigDecimal(500);

        cache.put(
                javax.money.MonetaryAmount.class,
                values(
                        Money.of(ten, BRAZILIAN_REAL),
                        Money.of(ten, US_DOLLAR),
                        Money.of(ten, EURO)));

        cache.put(javax.money.CurrencyUnit.class, values(BRAZILIAN_REAL, US_DOLLAR, EURO));

        cache.put(
                org.javamoney.moneta.Money.class,
                values(
                        Money.of(fiveHundred, BRAZILIAN_REAL),
                        Money.of(fiveHundred, US_DOLLAR),
                        Money.of(fiveHundred, EURO)));

        return cache;
    }
}
