package nl.jqno.equalsverifier.internal.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

public final class FieldNameExtractor {

    private FieldNameExtractor() {}

    public static <T> Set<String> extractFieldNames(Class<T> type) {
        Set<String> actualFieldNames = new HashSet<>();
        for (FieldProbe p : FieldIterable.of(type)) {
            String name = p.getName();
            actualFieldNames.add(name);
        }

        return Collections.unmodifiableSet(actualFieldNames);
    }
}
