package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;

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
     * @param tag The typetag of the type for which to create values.
     * @param valueProvider Repository for querying instances of generic types of the type tag.
     * @param attributes Provides metadata needed to provide a value and to keep track of recursion.
     * @return A "red" instance of {@code T}.
     */
    Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, Attributes attributes);
}
