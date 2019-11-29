package nl.jqno.equalsverifier.internal.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;

public final class FieldNameExtractor {

    private FieldNameExtractor() {}

    public static <T> Set<String> extractFieldNames(Class<T> type) {
        Set<String> actualFieldNames = new HashSet<>();
        for (Field f : FieldIterable.of(type)) {
            String name = f.getName();
            actualFieldNames.add(name);
        }

        return Collections.unmodifiableSet(actualFieldNames);
    }
}
