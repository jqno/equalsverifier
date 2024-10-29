package nl.jqno.equalsverifier.internal.testhelpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;

@SuppressFBWarnings(
    value = "DM_STRING_CTOR",
    justification = "We want to have a copy of the string"
)
public final class TestValueProvider implements ValueProvider {

    public static final Tuple<Integer> INTS = Tuple.of(3, 2, 3);
    public static final Tuple<String> STRINGS = Tuple.of("a", "b", new String("a"));

    public static final ValueProvider INSTANCE = new TestValueProvider(false);
    public static final ValueProvider EMPTY_INSTANCE = new TestValueProvider(true);

    private final Map<Class<?>, Tuple<?>> values = new HashMap<>();

    private TestValueProvider(boolean isEmpty) {
        if (isEmpty) {
            return;
        }
        values.put(int.class, INTS);
        values.put(Integer.class, INTS);
        values.put(String.class, STRINGS);
    }

    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
        @SuppressWarnings("unchecked")
        Tuple<T> result = (Tuple<T>) values.get(tag.getType());

        return Optional.ofNullable(result);
    }
}
