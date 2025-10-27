package nl.jqno.equalsverifier.internal.versionspecific;

import java.util.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.prefab.GenericValueSupplier;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public final class SequencedCollectionsValueSupplier<T> extends GenericValueSupplier<T> {

    public SequencedCollectionsValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(SequencedCollection.class)) {
            return collection(ArrayList::new);
        }
        if (is(SequencedSet.class)) {
            return collection(LinkedHashSet::new);
        }
        if (is(SequencedMap.class)) {
            return map(LinkedHashMap::new);
        }
        return Optional.empty();
    }
}
