package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

// CHECKSTYLE OFF: CyclomaticComplexity
// CHECKSTYLE OFF: NPathComplexity

public class PrimitiveValueSupplier<T> extends ValueSupplier<T> {
    public PrimitiveValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(boolean.class)) {
            return val(true, false, true);
        }
        if (is(byte.class)) {
            return val((byte) 1, (byte) 2, (byte) 1);
        }
        if (is(char.class)) {
            return val('a', 'b', 'a');
        }
        if (is(double.class)) {
            return val(0.5D, 1.0D, 0.5D);
        }
        if (is(float.class)) {
            return val(0.5F, 1.0F, 0.5F);
        }
        if (is(int.class)) {
            return val(1, 2, 1);
        }
        if (is(long.class)) {
            return val(1L, 2L, 1L);
        }
        if (is(short.class)) {
            return val((short) 1, (short) 2, (short) 1);
        }

        if (is(Boolean.class)) {
            return val(true, false, true);
        }
        if (is(Byte.class)) {
            return val((byte) 1, (byte) 2, (byte) 1);
        }
        if (is(Character.class)) {
            return val('α', 'ω', Character.valueOf('α'));
        }
        if (is(Double.class)) {
            return val(0.5D, 1.0D, Double.valueOf(0.5D));
        }
        if (is(Float.class)) {
            return val(0.5F, 1.0F, Float.valueOf(0.5F));
        }
        if (is(Integer.class)) {
            return val(1000, 2000, Integer.valueOf(1000));
        }
        if (is(Long.class)) {
            return val(1000L, 2000L, Long.valueOf(1000L));
        }
        if (is(Short.class)) {
            return val((short) 1000, (short) 2000, Short.valueOf((short) 1000));
        }

        return Optional.empty();
    }
}
