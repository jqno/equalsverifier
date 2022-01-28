package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/**
 * Wraps an object to provide access to it. ObjectAccessor can copy and scramble the wrapped object.
 *
 * @param <T> The specified object's class.
 */
public abstract class ObjectAccessor<T> {

    private final T object;
    private final Class<T> type;

    /** Package private constructor. Call {@link #of(Object)} to instantiate. */
    /* default */ObjectAccessor(T object, Class<T> type) {
        this.object = object;
        this.type = type;
    }

    /**
     * Factory method.
     *
     * @param <T> {@link #object}'s type.
     * @param object The object to wrap.
     * @return An {@link ObjectAccessor} for {@link #object}.
     */
    public static <T> ObjectAccessor<T> of(T object) {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) object.getClass();
        return of(object, type);
    }

    /**
     * Factory method.
     *
     * @param <T> {@link #object}'s type, or a supertype.
     * @param object The object to wrap.
     * @param type Superclass of {@link #object}'s type, as which it will be treated by {@link
     *     ObjectAccessor}.
     * @return An {@link ObjectAccessor} for {@link #object}.
     */
    public static <T> ObjectAccessor<T> of(T object, Class<T> type) {
        if (isRecord(type)) {
            return new RecordObjectAccessor<T>(object, type);
        }
        return new InPlaceObjectAccessor<>(object, type);
    }

    private static boolean isRecord(Class<?> type) {
        Class<?> record = Util.classForName("java.lang.Record");
        if (record == null) {
            return false;
        }
        return record.isAssignableFrom(type);
    }

    /**
     * Returns the wrapped object.
     *
     * @return The wrapped object.
     */
    public T get() {
        return object;
    }

    /**
     * Returns the type of the object.
     *
     * @return The type of the object.
     */
    public Class<T> type() {
        return type;
    }

    /**
     * Returns the value of the given field.
     *
     * @param field The field whose value we want to get.
     * @return The value of the given field.
     */
    @SuppressWarnings("unchecked")
    public T getField(Field field) {
        return (T) FieldAccessor.of(field).get(object);
    }

    /**
     * Creates a copy of the wrapped object.
     *
     * <p>Note: it does a "shallow" copy. Reference fields are not copied recursively.
     *
     * @return A shallow copy.
     */
    public abstract T copy();

    /**
     * Creates a copy of the wrapped object, where the copy's type is a specified subclass of the
     * wrapped object's class.
     *
     * <p>Note: it does a "shallow" copy. Reference fields are not copied recursively.
     *
     * @param subclass A subclass of the wrapped object's class.
     * @param <S> The subclass.
     * @return A shallow copy.
     */
    public abstract <S extends T> S copyIntoSubclass(Class<S> subclass);

    /**
     * Creates a copy of the wrapped object, where the copy type is an anonymous subclass of the
     * wrapped object's class.
     *
     * <p>Note: it does a "shallow" copy. Reference fields are not copied recursively.
     *
     * @return A shallow copy.
     */
    public abstract T copyIntoAnonymousSubclass();

    /**
     * Modifies all fields of the wrapped object that are declared in T and in its superclasses. It
     * may or may not mutate the object of the current ObjectAccessor. Either way, the current
     * ObjectAccessor and any reference to its object should be considered 'spent' after calling
     * this method. The returned ObjectAccessor can safely be used.
     *
     * <p>This method is consistent: given two equal objects; after scrambling both objects, they
     * remain equal to each other.
     *
     * <p>It may not be able to modify: 1. static final fields, and 2. final fields that are
     * initialized to a compile-time constant in the field declaration. These fields may be left
     * unmodified.
     *
     * @param prefabValues Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An accessor to the scrambled object.
     */
    public abstract ObjectAccessor<T> scramble(PrefabValues prefabValues, TypeTag enclosingType);

    /**
     * Modifies all fields of the wrapped object that are declared in T, but not those inherited
     * from superclasses. It may or may not mutate the object of the current ObjectAccessor. Either
     * way, the current ObjectAccessor and any reference to its object should be considered 'spent'
     * after calling this method. The returned ObjectAccessor can safely be used.
     *
     * <p>This method is consistent: given two equal objects; after scrambling both objects, they
     * remain equal to each other.
     *
     * <p>It may not be able to modify: 1. static final fields, and 2. final fields that are
     * initialized to a compile-time constant in the field declaration. These fields may be left
     * unmodified.
     *
     * @param prefabValues Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An accessor to the scrambled object.
     */
    public abstract ObjectAccessor<T> shallowScramble(
        PrefabValues prefabValues,
        TypeTag enclosingType
    );

    /**
     * Clears all fields of the wrapped object to their default values, but only if {@code
     * canBeDefault} for the given field returns true. Otherwise, leaves the value intact. It may or
     * may not mutate the object of the current ObjectAccessor. Either way, the current
     * ObjectAccessor and any reference to its object should be considered 'spent' after calling
     * this method. The returned ObjectAccessor can safely be used.
     *
     * <p>It may not be able to modify: 1. static final fields, and 2. final fields that are
     * initialized to a compile-time constant in the field declaration. These fields may be left
     * unmodified.
     *
     * @param canBeDefault A predicate that determines for the wrapped object's fields whether or
     *     not they are allowed to be 'default', i.e. 0 or null. If a field is marked with @NonNull,
     *     for example, it may not be default.
     * @param prefabValues Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An accessor to the cleared object.
     */
    public abstract ObjectAccessor<T> clear(
        Predicate<Field> canBeDefault,
        PrefabValues prefabValues,
        TypeTag enclosingType
    );

    /**
     * Clears the given field of the wrapped object to its default value. It may or may not mutate
     * the object of the current ObjectAccessor. Either way, the current ObjectAccessor and any
     * reference to its object should be considered 'spent' after calling this method. The returned
     * ObjectAccessor can safely be used.
     *
     * <p>It may not be able to modify: 1. static final fields, and 2. final fields that are
     * initialized to a compile-time constant in the field declaration. These fields may be left
     * unmodified.
     *
     * @param field The field to set to its default value.
     * @return An accessor to the object with the defaulted field.
     */
    public abstract ObjectAccessor<T> withDefaultedField(Field field);

    /**
     * Changes the given field of the wrapped object to some unspecified, but different value. It
     * may or may not mutate the object of the current ObjectAccessor. Either way, the current
     * ObjectAccessor and any reference to its object should be considered 'spent' after calling
     * this method. The returned ObjectAccessor can safely be used.
     *
     * <p>It may not be able to modify: 1. static final fields, and 2. final fields that are
     * initialized to a compile-time constant in the field declaration. These fields may be left
     * unmodified.
     *
     * @param field The field to set to a different value.
     * @param prefabValues Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An accessor to the object with the changed field.
     */
    public abstract ObjectAccessor<T> withChangedField(
        Field field,
        PrefabValues prefabValues,
        TypeTag enclosingType
    );

    /**
     * Changes the given field of the wrapped object to the given value. It may or may not mutate
     * the object of the current ObjectAccessor. Either way, the current ObjectAccessor and any
     * reference to its object should be considered 'spent' after calling this method. The returned
     * ObjectAccessor can safely be used.
     *
     * <p>It may not be able to modify: 1. static final fields, and 2. final fields that are
     * initialized to a compile-time constant in the field declaration. These fields may be left
     * unmodified.
     *
     * @param field The field to set to the given value.
     * @param newValue The value to set the field to.
     * @return An accessor to the object with the defaulted field.
     */
    public abstract ObjectAccessor<T> withFieldSetTo(Field field, Object newValue);
}
