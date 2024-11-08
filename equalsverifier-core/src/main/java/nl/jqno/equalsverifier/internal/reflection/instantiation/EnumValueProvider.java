package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.util.LinkedHashSet;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class EnumValueProvider implements ValueProvider {

    @Override
    public <T> Optional<Tuple<T>> provide(
        TypeTag tag,
        String label,
        LinkedHashSet<TypeTag> typeStack
    ) {
        Class<T> type = tag.getType();
        if (!type.isEnum()) {
            return Optional.empty();
        }

        T[] enumConstants = type.getEnumConstants();
        switch (enumConstants.length) {
            case 0:
                return Optional.of(new Tuple<>(null, null, null));
            case 1:
                return Optional.of(
                    new Tuple<>(enumConstants[0], enumConstants[0], enumConstants[0])
                );
            default:
                return Optional.of(
                    new Tuple<>(enumConstants[0], enumConstants[1], enumConstants[0])
                );
        }
    }
}
