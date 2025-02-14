package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.objenesis.Objenesis;

/**
 * Wraps an object to provide access to it. ObjectAccessor can copy and scramble the wrapped object.
 *
 * @param <T> The specified object's class.
 */
public abstract class ObjectAccessor<T> {

    private final T object;
    private final Class<T> type;

    /** Package private constructor. Call {@link #of(Object)} to instantiate. */
    /* default */ ObjectAccessor(T object, Class<T> type) {
        this.object = object;
        this.type = type;
    }

    /**
     * Factory method.
     *
     * @param <T>    {@link #object}'s type.
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
     * @param <T>    {@link #object}'s type, or a supertype.
     * @param object The object to wrap.
     * @param type   Superclass of {@link #object}'s type, as which it will be treated by {@link ObjectAccessor}.
     * @return An {@link ObjectAccessor} for {@link #object}.
     */
    public static <T> ObjectAccessor<T> of(T object, Class<T> type) {
        if (type.isRecord()) {
            return new RecordObjectAccessor<T>(object, type);
        }
        return new InPlaceObjectAccessor<>(object, type);
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
     * Creates a copy of the wrapped object.
     *
     * <p>
     * Note: it does a "shallow" copy. Reference fields are not copied recursively.
     *
     * @param objenesis Needed to instantiate the copy.
     * @return A shallow copy.
     */
    public abstract T copy(Objenesis objenesis);

    /**
     * Modifies all fields of the wrapped object that are declared in T and in its superclasses. It may or may not
     * mutate the object of the current ObjectAccessor. Either way, the current ObjectAccessor and any reference to its
     * object should be considered 'spent' after calling this method. The returned ObjectAccessor can safely be used.
     *
     * <p>
     * This method is consistent: given two equal objects; after scrambling both objects, they remain equal to each
     * other.
     *
     * <p>
     * It may not be able to modify: 1. static final fields, and 2. final fields that are initialized to a compile-time
     * constant in the field declaration. These fields may be left unmodified.
     *
     * @param valueProvider Prefabricated values to take values from.
     * @param enclosingType Describes the type that contains this object as a field, to determine any generic parameters
     *                          it may contain.
     * @param typeStack     Keeps track of recursion in the type.
     * @return An accessor to the scrambled object.
     */
    public abstract ObjectAccessor<T> scramble(
            VintageValueProvider valueProvider,
            TypeTag enclosingType,
            LinkedHashSet<TypeTag> typeStack);
}
