package nl.jqno.equalsverifier.internal.reflection.instantiation;

import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public interface ValueProvider {
    <T> Tuple<T> provide(TypeTag tag);
}
