package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;

/**
 * Instantiates and populates objects of a given class. {@link ClassAccessor} can create two
 * different instances of T, which are guaranteed not to be equal to each other, and which contain
 * no null values.
 *
 * @param <T> A class.
 */
public class ClassAccessor<T> {
    private final Class<T> type;
    private final PrefabValues prefabValues;

    /** Private constructor. Call {@link #of(Class, PrefabValues)} instead. */
    ClassAccessor(Class<T> type, PrefabValues prefabValues) {
        this.type = type;
        this.prefabValues = prefabValues;
    }

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link ClassAccessor} operates.
     * @param type The class on which {@link ClassAccessor} operates. Should be the same as T.
     * @param prefabValues Prefabricated values with which to fill instantiated objects.
     * @return A {@link ClassAccessor} for T.
     */
    public static <T> ClassAccessor<T> of(Class<T> type, PrefabValues prefabValues) {
        return new ClassAccessor<>(type, prefabValues);
    }

    /** @return The class on which {@link ClassAccessor} operates. */
    public Class<T> getType() {
        return type;
    }

    /**
     * Determines whether T declares a field. This does not include inherited fields.
     *
     * @param field The field that we want to detect.
     * @return True if T declares the field.
     */
    public boolean declaresField(Field field) {
        try {
            type.getDeclaredField(field.getName());
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Determines whether T has an {@code equals} method.
     *
     * @return True if T has an {@code equals} method.
     */
    public boolean declaresEquals() {
        return declaresMethod("equals", Object.class);
    }

    /**
     * Determines whether T has an {@code hashCode} method.
     *
     * @return True if T has an {@code hashCode} method.
     */
    public boolean declaresHashCode() {
        return declaresMethod("hashCode");
    }

    private boolean declaresMethod(String name, Class<?>... parameterTypes) {
        try {
            type.getDeclaredMethod(name, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Determines whether T's {@code equals} method is abstract.
     *
     * @return True if T's {@code equals} method is abstract.
     */
    public boolean isEqualsAbstract() {
        return isMethodAbstract("equals", Object.class);
    }

    /**
     * Determines whether T's {@code hashCode} method is abstract.
     *
     * @return True if T's {@code hashCode} method is abstract.
     */
    public boolean isHashCodeAbstract() {
        return isMethodAbstract("hashCode");
    }

    private boolean isMethodAbstract(String name, Class<?>... parameterTypes) {
        try {
            return Modifier.isAbstract(type.getMethod(name, parameterTypes).getModifiers());
        } catch (NoSuchMethodException e) {
            throw new ReflectionException("Should never occur (famous last words)");
        }
    }

    /**
     * Determines whether T's {@code equals} method is inherited from {@link Object}.
     *
     * @return true if T's {@code equals} method is inherited from {@link Object}; false if it is
     *     overridden in T or in any of its superclasses (except {@link Object}).
     */
    public boolean isEqualsInheritedFromObject() {
        ClassAccessor<? super T> i = this;
        while (i.getType() != Object.class) {
            if (i.declaresEquals() && !i.isEqualsAbstract()) {
                return false;
            }
            i = i.getSuperAccessor();
        }
        return true;
    }

    /**
     * Returns an accessor for T's superclass.
     *
     * @return An accessor for T's superclass.
     */
    public ClassAccessor<? super T> getSuperAccessor() {
        return ClassAccessor.of(type.getSuperclass(), prefabValues);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getBlackObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An instance of T.
     */
    public T getRedObject(TypeTag enclosingType) {
        return getRedAccessor(enclosingType).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     */
    public ObjectAccessor<T> getRedAccessor(TypeTag enclosingType) {
        ObjectAccessor<T> result = buildObjectAccessor();
        result.scramble(prefabValues, enclosingType);
        return result;
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An instance of T.
     */
    public T getBlackObject(TypeTag enclosingType) {
        return getBlackAccessor(enclosingType).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlackObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An {@link ObjectAccessor} for {@link #getBlackObject(TypeTag)}.
     */
    public ObjectAccessor<T> getBlackAccessor(TypeTag enclosingType) {
        ObjectAccessor<T> result = buildObjectAccessor();
        result.scramble(prefabValues, enclosingType);
        result.scramble(prefabValues, enclosingType);
        return result;
    }

    /**
     * Returns an {@link ObjectAccessor} for an instance of T where all the fields are initialized
     * to their default values. I.e., 0 for ints, and null for objects (except when the field is
     * marked with a NonNull annotation).
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param nonnullFields Fields which are not allowed to be set to null.
     * @param annotationCache To check for any NonNull annotations.
     * @return An {@link ObjectAccessor} for an instance of T where all the fields are initialized
     *     to their default values.
     */
    public ObjectAccessor<T> getDefaultValuesAccessor(
            TypeTag enclosingType, Set<String> nonnullFields, AnnotationCache annotationCache) {
        ObjectAccessor<T> result = buildObjectAccessor();
        for (Field field : FieldIterable.of(type)) {
            if (NonnullAnnotationVerifier.fieldIsNonnull(field, annotationCache)
                    || nonnullFields.contains(field.getName())) {
                FieldAccessor accessor = result.fieldAccessorFor(field);
                accessor.changeField(prefabValues, enclosingType);
            }
        }
        return result;
    }

    private ObjectAccessor<T> buildObjectAccessor() {
        T object = Instantiator.of(type).instantiate();
        return ObjectAccessor.of(object);
    }
}
