package equalsverifier.prefabvalues.factoryproviders;

import equalsverifier.prefabvalues.FactoryCache;
import org.joda.time.*;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;

import static equalsverifier.prefabvalues.factories.Factories.values;

public final class JodaFactoryProvider implements FactoryProvider {

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(
            Chronology.class,
            values(GregorianChronology.getInstanceUTC(), ISOChronology.getInstanceUTC(), GregorianChronology.getInstanceUTC()));
        cache.put(
            DateTimeZone.class,
            values(DateTimeZone.forOffsetHours(+1), DateTimeZone.forOffsetHours(-10), DateTimeZone.forOffsetHours(+1)));
        cache.put(
            PeriodType.class,
            values(PeriodType.days(), PeriodType.hours(), PeriodType.days()));
        cache.put(
            YearMonth.class,
            values(new YearMonth(2018, 5), new YearMonth(2014, 7), new YearMonth(2018, 5)));
        cache.put(
            MonthDay.class,
            values(new MonthDay(6, 1), new MonthDay(6, 26), new MonthDay(6, 1)));

        return cache;
    }
}
