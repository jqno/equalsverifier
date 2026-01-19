package nl.jqno.equalsverifier.internal.valueproviders.prefab;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class JavaMathValueSupplier<T> extends ValueSupplier<T> {
    public JavaMathValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(BigDecimal.class)) {
            return val(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO);
        }
        if (is(BigInteger.class)) {
            return val(BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO);
        }

        return Optional.empty();
    }

}
