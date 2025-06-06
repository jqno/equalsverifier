package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Creates instances of generic types for use as prefab value.
 *
 * @param <T> The type to instantiate.
 */
@FunctionalInterface
public interface PrefabValueFactory<T> {
    /**
     * Creates a tuple of two prefab values.
     *
     * @param tag           The typetag of the type for which to create values.
     * @param valueProvider Repository for querying instances of generic types of the type tag.
     * @param typeStack     A stack of {@link TypeTag}s that require tag in order to be created. Used for recursion
     *                          detection.
     * @return A "red" instance of {@code T}.
     */
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack);
}
