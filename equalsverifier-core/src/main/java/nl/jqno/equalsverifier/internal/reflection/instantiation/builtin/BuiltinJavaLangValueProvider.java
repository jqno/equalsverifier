package nl.jqno.equalsverifier.internal.reflection.instantiation.builtin;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

public class BuiltinJavaLangValueProvider implements ValueProvider {

    @SuppressFBWarnings(
        value = "DM_STRING_CTOR",
        justification = "We want to make an actual copy of a String"
    )
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        return or(
            // Primitives
            attempt(tag, boolean.class, true, false, true),
            attempt(tag, byte.class, (byte) 1, (byte) 2, (byte) 3),
            attempt(tag, char.class, 'a', 'b', 'a'),
            attempt(tag, double.class, 0.5D, 1.0D, 0.5D),
            attempt(tag, float.class, 0.5F, 1.0F, 0.5F),
            attempt(tag, int.class, 1, 2, 1),
            attempt(tag, long.class, 1L, 2L, 1L),
            attempt(tag, short.class, (short) 1, (short) 2, (short) 1),
            // Boxed types
            attempt(tag, Boolean.class, true, false, true),
            attempt(tag, Byte.class, (byte) 1, (byte) 2, (byte) 1),
            attempt(tag, Character.class, 'α', 'ω', Character.valueOf('α')),
            attempt(tag, Double.class, 0.5D, 1.0D, Double.valueOf(0.5D)),
            attempt(tag, Float.class, 0.5F, 1.0F, Float.valueOf(0.5F)),
            attempt(tag, Integer.class, 1000, 2000, Integer.valueOf(1000)),
            attempt(tag, Long.class, 1000L, 2000L, Long.valueOf(1000L)),
            attempt(tag, Short.class, (short) 1000, (short) 2000, Short.valueOf((short) 1000)),
            // Special classes
            attempt(tag, Object.class, new Object(), new Object(), new Object()),
            attempt(tag, String.class, "one", "two", new String("one")),
            attempt(tag, Enum.class, Dummy.RED, Dummy.BLUE, Dummy.RED)
        );
    }

    private <T> Optional<Tuple<T>> attempt(
        TypeTag tag,
        Class<T> type,
        Object red,
        Object blue,
        Object redCopy
    ) {
        if (tag.getType().equals(type)) {
            return Optional.of(Tuple.of(red, blue, redCopy));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private static <T> Optional<T> or(Optional<?>... optionals) {
        return (Optional<T>) Stream
            .of(optionals)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    private enum Dummy {
        RED,
        BLUE
    }
}
