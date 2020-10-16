package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterable to iterate over all declared fields in a class and, if needed, over all declared fields
 * of its superclasses.
 */
public final class FieldIterable implements Iterable<Field> {
    private final Class<?> type;
    private final boolean includeSuperclasses;
    private final boolean includeStatic;

    /** Private constructor. Call {@link #of(Class)} or {@link #ofIgnoringSuper(Class)} instead. */
    private FieldIterable(Class<?> type, boolean includeSuperclasses, boolean includeStatic) {
        this.type = type;
        this.includeSuperclasses = includeSuperclasses;
        this.includeStatic = includeStatic;
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type} and
     * over the declared fields of all of its superclasses.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable of(Class<?> type) {
        return new FieldIterable(type, true, true);
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type},
     * but that ignores the declared fields of its superclasses.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable ofIgnoringSuper(Class<?> type) {
        return new FieldIterable(type, false, true);
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type},
     * but that ignores its static fields.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable ofIgnoringStatic(Class<?> type) {
        return new FieldIterable(type, true, false);
    }

    /**
     * Returns an iterator over all declared fields of the class and all of its superclasses.
     *
     * @return An iterator over all declared fields of the class and all of its superclasses.
     */
    @Override
    public Iterator<Field> iterator() {
        return createFieldList().iterator();
    }

    private List<Field> createFieldList() {
        List<Field> result = new ArrayList<>();

        result.addAll(addFieldsFor(type));

        if (includeSuperclasses) {
            for (Class<?> c : SuperclassIterable.of(type)) {
                result.addAll(addFieldsFor(c));
            }
        }

        return result;
    }

    private List<Field> addFieldsFor(Class<?> c) {
        List<Field> fields = new ArrayList<>();
        List<Field> statics = new ArrayList<>();

        for (Field field : c.getDeclaredFields()) {
            if (!field.isSynthetic() && !"__cobertura_counters".equals(field.getName())) {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (isStatic && includeStatic) {
                    statics.add(field);
                }
                if (!isStatic) {
                    fields.add(field);
                }
            }
        }

        List<Field> result = new ArrayList<>();
        result.addAll(fields);
        result.addAll(statics);
        return result;
    }
}
