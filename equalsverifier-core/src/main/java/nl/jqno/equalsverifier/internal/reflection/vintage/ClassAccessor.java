package nl.jqno.equalsverifier.internal.reflection.vintage;

import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import org.objenesis.Objenesis;

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
    private final Objenesis objenesis;

    /** Private constructor. Call {@link #of(Class, PrefabValues)} instead. */
    ClassAccessor(Class<T> type, VintageValueProvider valueProvider, Objenesis objenesis) {
        this.type = type;
        this.valueProvider = valueProvider;
        this.objenesis = objenesis;
    }

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link ClassAccessor} operates.
     * @param type The class on which {@link ClassAccessor} operates. Should be the same as T.
     * @param valueProvider Prefabricated values with which to fill instantiated objects.
     * @param objenesis To instantiate non-record classes.
     * @return A {@link ClassAccessor} for T.
     */
    public static <T> ClassAccessor<T> of(
        Class<T> type,
        VintageValueProvider valueProvider,
        Objenesis objenesis
    ) {
        return new ClassAccessor<>(type, valueProvider, objenesis);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getBlueObject(TypeTag, Attributes)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param attributes Provides metadata needed to provide a value and to keep track of recursion.
     * @return An instance of T.
     */
    public T getRedObject(TypeTag enclosingType, Attributes attributes) {
        return getRedAccessor(enclosingType, attributes).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getRedObject(TypeTag, Attributes)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param attributes Provides metadata needed to provide a value and to keep track of recursion.
     * @return An {@link ObjectAccessor} for {@link #getRedObject}.
     */
    public ObjectAccessor<T> getRedAccessor(TypeTag enclosingType, Attributes attributes) {
        return buildObjectAccessor().scramble(valueProvider, enclosingType, attributes);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getRedObject(TypeTag, Attributes)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param attributes Provides metadata needed to provide a value and to keep track of recursion.
     * @return An instance of T.
     */
    public T getBlueObject(TypeTag enclosingType, Attributes attributes) {
        return getBlueAccessor(enclosingType, attributes).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlueObject(TypeTag, Attributes)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param attributes Provides metadata needed to provide a value and to keep track of recursion.
     * @return An {@link ObjectAccessor} for {@link #getBlueObject(TypeTag, Attributes)}.
     */
    public ObjectAccessor<T> getBlueAccessor(TypeTag enclosingType, Attributes attributes) {
        return buildObjectAccessor()
            .scramble(valueProvider, enclosingType, attributes)
            .scramble(valueProvider, enclosingType, attributes);
    }

    private ObjectAccessor<T> buildObjectAccessor() {
        T object = Instantiator.of(type, objenesis).instantiate();
        return ObjectAccessor.of(object);
    }
}
