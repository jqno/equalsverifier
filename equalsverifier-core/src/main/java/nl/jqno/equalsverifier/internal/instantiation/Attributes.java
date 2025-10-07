package nl.jqno.equalsverifier.internal.instantiation;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public record Attributes(String fieldName, LinkedHashSet<TypeTag> typeStack) {
    public static Attributes empty() {
        return new Attributes(null, new LinkedHashSet<>());
    }

    public static Attributes named(String fieldName) {
        return new Attributes(fieldName, new LinkedHashSet<>());
    }
}
