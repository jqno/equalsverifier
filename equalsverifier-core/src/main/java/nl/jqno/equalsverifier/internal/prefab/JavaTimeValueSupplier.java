package nl.jqno.equalsverifier.internal.prefab;

import java.time.*;
import java.util.Optional;
import java.util.TimeZone;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

class JavaTimeValueSupplier<T> extends ValueSupplier<T> {
    public JavaTimeValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Clock.class)) {
            return val(Clock.systemUTC(), Clock.system(ZoneId.of("-10")), Clock.systemUTC());
        }
        if (is(Duration.class)) {
            return val(Duration.ZERO, Duration.ofDays(1L), Duration.ZERO);
        }
        if (is(Instant.class)) {
            return val(Instant.MIN, Instant.MAX, Instant.MIN);
        }
        if (is(LocalDateTime.class)) {
            return val(LocalDateTime.MIN, LocalDateTime.MAX, LocalDateTime.MIN);
        }
        if (is(LocalDate.class)) {
            return val(LocalDate.MIN, LocalDate.MAX, LocalDate.MIN);
        }
        if (is(LocalTime.class)) {
            return val(LocalTime.MIN, LocalTime.MAX, LocalTime.MIN);
        }
        if (is(MonthDay.class)) {
            return val(MonthDay.of(1, 1), MonthDay.of(12, 31), MonthDay.of(1, 1));
        }
        if (is(OffsetDateTime.class)) {
            return val(OffsetDateTime.MIN, OffsetDateTime.MAX, OffsetDateTime.MIN);
        }
        if (is(OffsetTime.class)) {
            return val(OffsetTime.MIN, OffsetTime.MAX, OffsetTime.MIN);
        }
        if (is(Period.class)) {
            return val(Period.ZERO, Period.of(1, 1, 1), Period.ZERO);
        }
        if (is(TimeZone.class)) {
            return val(TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"), TimeZone.getTimeZone("GMT+1"));
        }
        if (is(Year.class)) {
            return val(Year.of(2000), Year.of(2010), Year.of(2000));
        }
        if (is(YearMonth.class)) {
            return val(YearMonth.of(2000, 1), YearMonth.of(2010, 12), YearMonth.of(2000, 1));
        }
        if (is(ZoneId.class)) {
            return val(ZoneId.of("+1"), ZoneId.of("-10"), ZoneId.of("+1"));
        }
        if (is(ZoneOffset.class)) {
            return val(ZoneOffset.ofHours(1), ZoneOffset.ofHours(-1), ZoneOffset.ofHours(1));
        }
        if (is(ZonedDateTime.class)) {
            return val(
                ZonedDateTime.parse("2017-12-13T10:15:30+01:00"),
                ZonedDateTime.parse("2016-11-12T09:14:29-01:00"),
                ZonedDateTime.parse("2017-12-13T10:15:30+01:00"));
        }

        return Optional.empty();
    }
}
