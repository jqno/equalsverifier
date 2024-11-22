package nl.jqno.equalsverifier.internal.testhelpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

@SuppressFBWarnings(
    value = "DM_STRING_CTOR",
    justification = "We really do need a separate instances with the same value"
)
public final class TestValueProviders {

    public static final Tuple<Integer> INTS = Tuple.of(42, 1337, 42);
    public static final Tuple<String> STRINGS = Tuple.of("abc", "xyz", new String("abc"));

    private TestValueProviders() {}

    public static ValueProvider empty() {
        return new ValueProvider() {
            @Override
            public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
                return Optional.empty();
            }
        };
    }

    public static ValueProvider simple() {
        return new ValueProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
                if (tag.getType().equals(int.class)) {
                    return Optional.of((Tuple<T>) INTS);
                }
                if (tag.getType().equals(Integer.class)) {
                    return Optional.of((Tuple<T>) INTS);
                }
                if (tag.getType().equals(String.class)) {
                    return Optional.of((Tuple<T>) STRINGS);
                }
                return Optional.empty();
            }
        };
    }
}
