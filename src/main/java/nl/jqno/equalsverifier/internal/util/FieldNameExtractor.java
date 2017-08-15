package nl.jqno.equalsverifier.internal.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;

public class FieldNameExtractor {

    public static Set<String> extractFields(Class type) {

        Set<String> actualFieldNames = new HashSet<>();
        for (Field f : FieldIterable.of(type)) {
            String name = f.getName();
            actualFieldNames.add(name);
        }

        return actualFieldNames;
    }

}
