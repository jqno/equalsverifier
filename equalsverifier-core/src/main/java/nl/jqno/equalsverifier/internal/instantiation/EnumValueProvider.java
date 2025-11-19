package nl.jqno.equalsverifier.internal.instantiation;

import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * A ValueProvider for enums.
 */
public class EnumValueProvider implements ValueProvider {

    /** {@inheritDoc}} */
    @Override
    public <T> Optional<Tuple<T>> provide(TypeTag tag, Attributes attributes) {
        Class<T> type = tag.getType();
        if (!type.isEnum()) {
            return Optional.empty();
        }

        T[] enumConstants = type.getEnumConstants();
        var tup = switch (enumConstants.length) {
            case 0 -> new Tuple<T>(null, null, null);
            case 1 -> new Tuple<>(enumConstants[0], enumConstants[0], enumConstants[0]);
            default -> new Tuple<>(enumConstants[0], enumConstants[1], enumConstants[0]);
        };

        return Optional.of(tup);
    }
}
