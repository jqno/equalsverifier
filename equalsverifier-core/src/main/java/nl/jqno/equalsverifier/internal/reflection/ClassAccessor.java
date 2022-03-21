package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Predicate;
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
     * Determines whether T is a Java Record.
     *
     * @return true if T is a Java Record.
     */
    public boolean isRecord() {
        return RecordsHelper.isRecord(type);
    }

    /**
     * Determines whether T is a sealed class.
     *
     * @return true if T is a sealed class
     */
    public boolean isSealed() {
        return SealedClassesHelper.isSealed(type);
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
        return rethrow(() ->
            Modifier.isAbstract(type.getMethod(name, parameterTypes).getModifiers())
        );
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
     * #getBlueObject(TypeTag)}.
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
        return buildObjectAccessor().scramble(prefabValues, enclosingType);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An instance of T.
     */
    public T getBlueObject(TypeTag enclosingType) {
        return getBlueAccessor(enclosingType).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlueObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An {@link ObjectAccessor} for {@link #getBlueObject(TypeTag)}.
     */
    public ObjectAccessor<T> getBlueAccessor(TypeTag enclosingType) {
        return buildObjectAccessor()
            .scramble(prefabValues, enclosingType)
            .scramble(prefabValues, enclosingType);
    }

    /**
     * Returns an {@link ObjectAccessor} for an instance of T where all the fields are initialized
     * to their default values. I.e., 0 for ints, and null for objects (except when the field is
     * marked with a NonNull annotation).
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param isWarningNullSuppressed Whether reference fields must be non-null (a.k.a., whether
     *     Warnings.NULL_FIELDS is suppressed).
     * @param isWarningZeroSuppressed Whether primitive fields must be non-0 (a.k.a., whether
     *     Warnings.ZERO_FIELDS is suppressed).
     * @param nonnullFields Fields which are not allowed to be set to null.
     * @param annotationCache To check for any NonNull annotations.
     * @return An {@link ObjectAccessor} for an instance of T where all the fields are initialized
     *     to their default values.
     */
    public ObjectAccessor<T> getDefaultValuesAccessor(
        TypeTag enclosingType,
        boolean isWarningNullSuppressed,
        boolean isWarningZeroSuppressed,
        Set<String> nonnullFields,
        AnnotationCache annotationCache
    ) {
        Predicate<Field> canBeDefault = f ->
            canBeDefault(
                f,
                enclosingType,
                isWarningNullSuppressed,
                isWarningZeroSuppressed,
                nonnullFields,
                annotationCache
            );
        return buildObjectAccessor().clear(canBeDefault, prefabValues, enclosingType);
    }

    private boolean canBeDefault(
        Field f,
        TypeTag enclosingType,
        boolean isWarningNullSuppressed,
        boolean isWarningZeroSuppressed,
        Set<String> nonnullFields,
        AnnotationCache annotationCache
    ) {
        FieldAccessor accessor = FieldAccessor.of(f);
        if (accessor.fieldIsPrimitive()) {
            return !isWarningZeroSuppressed;
        }

        boolean isAnnotated = NonnullAnnotationVerifier.fieldIsNonnull(f, annotationCache);
        boolean isMentionedExplicitly = nonnullFields.contains(f.getName());
        return !isWarningNullSuppressed && !isAnnotated && !isMentionedExplicitly;
    }

    private ObjectAccessor<T> buildObjectAccessor() {
        T object = Instantiator.of(type).instantiate();
        return ObjectAccessor.of(object);
    }
}
