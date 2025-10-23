package nl.jqno.equalsverifier.internal.versionspecific;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.prefab.GenericValueSupplier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class SequencedCollectionsValueSupplier<T> extends GenericValueSupplier<T> {

    public SequencedCollectionsValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(SequencedCollection.class)) {
            return collection(tag, attributes, ArrayList::new);
        }
        if (is(SequencedSet.class)) {
            return collection(tag, attributes, LinkedHashSet::new);
        }
        if (is(SequencedMap.class)) {
            return map(tag, attributes, LinkedHashMap::new);
        }
        return Optional.empty();
    }
}
