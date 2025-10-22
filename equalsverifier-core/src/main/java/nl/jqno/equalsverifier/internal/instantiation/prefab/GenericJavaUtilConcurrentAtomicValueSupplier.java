package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.concurrent.atomic.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericJavaUtilConcurrentAtomicValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaUtilConcurrentAtomicValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(AtomicMarkableReference.class)) {
            return generic(tag, attributes, val -> (T) new AtomicMarkableReference<>(val, true));
        }
        if (is(AtomicReference.class)) {
            return generic(tag, attributes, val -> (T) new AtomicReference<>(val));
        }
        if (is(AtomicStampedReference.class)) {
            return generic(tag, attributes, val -> (T) new AtomicStampedReference<>(val, 0));
        }
        if (is(AtomicReferenceArray.class)) {
            return generic(tag, attributes, val -> (T) new AtomicReferenceArray<>(new Object[] { val }));
        }
        return Optional.empty();
    }
}
