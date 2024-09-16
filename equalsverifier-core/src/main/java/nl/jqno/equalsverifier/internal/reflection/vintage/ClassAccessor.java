package nl.jqno.equalsverifier.internal.reflection.vintage;

import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;

/**
 * Instantiates and populates objects of a given class. {@link ClassAccessor} can create two
 * different instances of T, which are guaranteed not to be equal to each other, and which contain
 * no null values.
 *
 * @param <T> A class.
 */
public class ClassAccessor<T> {

    private final Class<T> type;
    private final VintageValueProvider valueProvider;

    /** Private constructor. Call {@link #of(Class, PrefabValues)} instead. */
    ClassAccessor(Class<T> type, VintageValueProvider valueProvider) {
        this.type = type;
        this.valueProvider = valueProvider;
    }

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link ClassAccessor} operates.
     * @param type The class on which {@link ClassAccessor} operates. Should be the same as T.
     * @param valueProvider Prefabricated values with which to fill instantiated objects.
     * @return A {@link ClassAccessor} for T.
     */
    public static <T> ClassAccessor<T> of(Class<T> type, VintageValueProvider valueProvider) {
        return new ClassAccessor<>(type, valueProvider);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getBlueObject(TypeTag, LinkedHashSet)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An instance of T.
     */
    public T getRedObject(TypeTag enclosingType, LinkedHashSet<TypeTag> typeStack) {
        return getRedAccessor(enclosingType, typeStack).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getRedObject(TypeTag, LinkedHashSet)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An {@link ObjectAccessor} for {@link #getRedObject}.
     */
    public ObjectAccessor<T> getRedAccessor(
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return buildObjectAccessor().scramble(valueProvider, enclosingType, typeStack);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getRedObject(TypeTag, LinkedHashSet)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An instance of T.
     */
    public T getBlueObject(TypeTag enclosingType, LinkedHashSet<TypeTag> typeStack) {
        return getBlueAccessor(enclosingType, typeStack).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlueObject(TypeTag, LinkedHashSet)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An {@link ObjectAccessor} for {@link #getBlueObject(TypeTag, LinkedHashSet)}.
     */
    public ObjectAccessor<T> getBlueAccessor(
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return buildObjectAccessor()
            .scramble(valueProvider, enclosingType, typeStack)
            .scramble(valueProvider, enclosingType, typeStack);
    }

    private ObjectAccessor<T> buildObjectAccessor() {
        T object = Instantiator.of(type).instantiate();
        return ObjectAccessor.of(object);
    }
}
