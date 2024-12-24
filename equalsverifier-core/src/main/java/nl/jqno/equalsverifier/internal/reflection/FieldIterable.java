package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Iterable to iterate over all declared fields in a class and, if needed, over all declared fields of its superclasses.
 */
public final class FieldIterable implements Iterable<FieldProbe> {

    private final Class<?> type;
    private final boolean includeSuperclasses;
    private final boolean includeStatic;
    private final boolean isKotlin;

    /** Private constructor. Call {@link #of(Class)} or {@link #ofIgnoringSuper(Class)} instead. */
    private FieldIterable(Class<?> type, boolean includeSuperclasses, boolean includeStatic, boolean isKotlin) {
        this.type = type;
        this.includeSuperclasses = includeSuperclasses;
        this.includeStatic = includeStatic;
        this.isKotlin = isKotlin;
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type} and over the declared
     * fields of all of its superclasses.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable of(Class<?> type) {
        return new FieldIterable(type, true, true, false);
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type} and over the declared
     * fields of all of its superclasses, but that ignores overridden Kotlin backing fields in superclasses.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable ofKotlin(Class<?> type) {
        return new FieldIterable(type, true, true, true);
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type}, but that ignores the
     * declared fields of its superclasses.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable ofIgnoringSuper(Class<?> type) {
        return new FieldIterable(type, false, true, false);
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type}, but that ignores its
     * static fields.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable ofIgnoringStatic(Class<?> type) {
        return new FieldIterable(type, true, false, false);
    }

    /**
     * Factory method for a FieldIterable that iterates over all declared fields of {@code type}, but that ignores its
     * the declared fields of its superclasses, as well as its static fields.
     *
     * @param type The class that contains the fields over which to iterate.
     * @return A FieldIterable.
     */
    public static FieldIterable ofIgnoringSuperAndStatic(Class<?> type) {
        return new FieldIterable(type, false, false, false);
    }

    /**
     * Returns an iterator over all declared fields of the class and all of its superclasses.
     *
     * @return An iterator over all declared fields of the class and all of its superclasses.
     */
    @Override
    public Iterator<FieldProbe> iterator() {
        return createFieldList().iterator();
    }

    private List<FieldProbe> createFieldList() {
        if (isKotlin) {
            return createKotlinFieldList();
        }
        else {
            return createJavaFieldList();
        }
    }

    private List<FieldProbe> createJavaFieldList() {
        var result = new ArrayList<FieldProbe>();
        result.addAll(addFieldsFor(type));

        if (includeSuperclasses) {
            for (Class<?> c : SuperclassIterable.of(type)) {
                result.addAll(addFieldsFor(c));
            }
        }

        return result;
    }

    private List<FieldProbe> createKotlinFieldList() {
        var result = new ArrayList<FieldProbe>();
        result.addAll(addFieldsFor(type));
        var names = result.stream().map(FieldProbe::getName).collect(Collectors.toSet());

        if (includeSuperclasses) {
            for (Class<?> c : SuperclassIterable.of(type)) {
                List<FieldProbe> superFields =
                        addFieldsFor(c).stream().filter(p -> !names.contains(p.getName())).collect(Collectors.toList());
                result.addAll(superFields);
                superFields.stream().map(FieldProbe::getName).forEach(names::add);
            }
        }

        return result;
    }

    private List<FieldProbe> addFieldsFor(Class<?> c) {
        var fields = new ArrayList<FieldProbe>();
        var statics = new ArrayList<FieldProbe>();

        for (Field field : c.getDeclaredFields()) {
            if (!field.isSynthetic()
                    && !"__cobertura_counters".equals(field.getName())
                    && !field.getName().startsWith("bitmap$init$") // Generated by Scala 2.x's -Xcheckinit flag
            ) {
                FieldProbe probe = FieldProbe.of(field);
                boolean isStatic = probe.isStatic();
                if (isStatic && includeStatic) {
                    statics.add(probe);
                }
                if (!isStatic) {
                    fields.add(probe);
                }
            }
        }

        var result = new ArrayList<FieldProbe>();
        result.addAll(fields);
        result.addAll(statics);
        return result;
    }
}
