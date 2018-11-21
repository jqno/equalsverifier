package equalsverifier.reflection;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;

import java.lang.reflect.Field;

/**
 * Wraps an object to provide reflective access to it. ObjectAccessor can
 * copy and scramble the wrapped object.
 *
 * @param <T> The specified object's class.
 */
public final class ObjectAccessor<T> {
    private final T object;
    private final Class<T> type;

    /**
     * Private constructor. Call {@link #of(Object)} to instantiate.
     */
    private ObjectAccessor(T object, Class<T> type) {
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
        Class<T> type = (Class<T>)object.getClass();
        return new ObjectAccessor<>(object, type);
    }

    /**
     * Factory method.
     *
     * @param <T> {@link #object}'s type, or a supertype.
     * @param object The object to wrap.
     * @param type Superclass of {@link #object}'s type, as which it will be
     *          treated by {@link ObjectAccessor}.
     * @return An {@link ObjectAccessor} for {@link #object}.
     */
    public static <T> ObjectAccessor<T> of(T object, Class<T> type) {
        return new ObjectAccessor<>(object, type);
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
     * Returns a FieldAccessor for the wrapped object and the specified field.
     *
     * @param field A field in T.
     * @return A FieldAccessor for the wrapped object and the specified field.
     */
    public FieldAccessor fieldAccessorFor(Field field) {
        return new FieldAccessor(object, field);
    }

    /**
     * Creates a copy of the wrapped object.
     *
     * Note: it does a "shallow" copy. Reference fields are not copied
     * recursively.
     *
     * @return A shallow copy.
     */
    public T copy() {
        T copy = Instantiator.of(type).instantiate();
        return copyInto(copy);
    }

    /**
     * Creates a copy of the wrapped object, where the copy's type is a
     * specified subclass of the wrapped object's class.
     *
     * Note: it does a "shallow" copy. Reference fields are not copied
     * recursively.
     *
     * @param subclass A subclass of the wrapped object's class.
     * @param <S> The subclass.
     * @return A shallow copy.
     */
    public <S extends T> S copyIntoSubclass(Class<S> subclass) {
        S copy = Instantiator.of(subclass).instantiate();
        return copyInto(copy);
    }

    /**
     * Creates a copy of the wrapped object, where the copy type is an
     * anonymous subclass of the wrapped object's class.
     *
     * Note: it does a "shallow" copy. Reference fields are not copied
     * recursively.
     *
     * @return A shallow copy.
     */
    public T copyIntoAnonymousSubclass() {
        T copy = Instantiator.of(type).instantiateAnonymousSubclass();
        return copyInto(copy);
    }

    private <S> S copyInto(S copy) {
        for (Field field : FieldIterable.of(type)) {
            FieldAccessor accessor = new FieldAccessor(object, field);
            accessor.copyTo(copy);
        }
        return copy;
    }

    /**
     * Modifies all fields of the wrapped object that are declared in T and in
     * its superclasses.
     *
     * This method is consistent: given two equal objects; after scrambling
     * both objects, they remain equal to each other.
     *
     * It cannot modifiy:
     * 1. static final fields, and
     * 2. final fields that are initialized to a compile-time constant in the
     *      field declaration.
     * These fields will be left unmodified.
     *
     * @param prefabAbstract Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     */
    public void scramble(PrefabAbstract prefabAbstract, TypeTag enclosingType) {
        for (Field field : FieldIterable.of(type)) {
            FieldAccessor accessor = new FieldAccessor(object, field);
            accessor.changeField(prefabAbstract, enclosingType);
        }
    }

    /**
     * Modifies all fields of the wrapped object that are declared in T, but
     * not those inherited from superclasses.
     *
     * This method is consistent: given two equal objects; after scrambling
     * both objects, they remain equal to each other.
     *
     * It cannot modifiy:
     * 1. static final fields, and
     * 2. final fields that are initialized to a compile-time constant in the
     *      field declaration.
     * These fields will be left unmodified.
     *
     * @param prefabAbstract Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     */
    public void shallowScramble(PrefabAbstract prefabAbstract, TypeTag enclosingType) {
        for (Field field : FieldIterable.ofIgnoringSuper(type)) {
            FieldAccessor accessor = new FieldAccessor(object, field);
            accessor.changeField(prefabAbstract, enclosingType);
        }
    }
}
