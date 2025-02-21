package nl.jqno.equalsverifier.internal.prefab;

import java.util.*;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

public class JavaUtilValueSupplier<T> extends ValueSupplier<T> {
    public JavaUtilValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Calendar.class)) {
            return val(
                new GregorianCalendar(2010, Calendar.AUGUST, 4),
                new GregorianCalendar(2010, Calendar.AUGUST, 5),
                new GregorianCalendar(2010, Calendar.AUGUST, 4));
        }
        if (is(Currency.class)) {
            return val(Currency.getInstance("EUR"), Currency.getInstance("GBP"), Currency.getInstance("EUR"));
        }
        if (is(Date.class)) {
            return val(new Date(0), new Date(1), new Date(0));
        }
        if (is(DoubleSummaryStatistics.class)) {
            var redDoubleStats = new DoubleSummaryStatistics();
            var blueDoubleStats = new DoubleSummaryStatistics();
            return val(redDoubleStats, blueDoubleStats, redDoubleStats);
        }
        if (is(Formatter.class)) {
            return val(new Formatter(), new Formatter(), new Formatter());
        }
        if (is(GregorianCalendar.class)) {
            return val(
                new GregorianCalendar(2010, Calendar.AUGUST, 4),
                new GregorianCalendar(2010, Calendar.AUGUST, 5),
                new GregorianCalendar(2010, Calendar.AUGUST, 4));
        }
        if (is(IntSummaryStatistics.class)) {
            var redIntStats = new IntSummaryStatistics();
            var blueIntStats = new IntSummaryStatistics();
            return val(redIntStats, blueIntStats, redIntStats);
        }
        if (is(Locale.class)) {
            return val(new Locale("nl"), new Locale("hu"), new Locale("nl"));
        }
        if (is(LongSummaryStatistics.class)) {
            var redLongStats = new LongSummaryStatistics();
            var blueLongStats = new LongSummaryStatistics();
            return val(redLongStats, blueLongStats, redLongStats);
        }
        if (is(OptionalDouble.class)) {
            return val(OptionalDouble.of(0.5), OptionalDouble.of(1.0), OptionalDouble.of(0.5));
        }
        if (is(OptionalInt.class)) {
            return val(OptionalInt.of(1), OptionalInt.of(2), OptionalInt.of(1));
        }
        if (is(OptionalLong.class)) {
            return val(OptionalLong.of(1), OptionalLong.of(2), OptionalLong.of(1));
        }
        if (is(Scanner.class)) {
            return val(new Scanner("one"), new Scanner("two"), new Scanner("one"));
        }
        if (is(UUID.class)) {
            return val(new UUID(0, -1), new UUID(1, 0), new UUID(0, -1));
        }

        return Optional.empty();
    }

}
