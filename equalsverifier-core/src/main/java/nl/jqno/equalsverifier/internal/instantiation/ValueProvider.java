package nl.jqno.equalsverifier.internal.instantiation;

import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

public interface ValueProvider {
    <T> Tuple<T> provide(TypeTag tag);
}
