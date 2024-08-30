package nl.jqno.equalsverifier.internal.instantiation;

import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

public interface InstanceCreator {
    <T> Tuple<T> instantiate(TypeTag tag);
}
