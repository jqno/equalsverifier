package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.concurrent.atomic.*;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class GenericJavaUtilConcurrentAtomicValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericJavaUtilConcurrentAtomicValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(AtomicMarkableReference.class)) {
            return generic(val -> new AtomicMarkableReference<>(val, true));
        }
        if (is(AtomicReference.class)) {
            return generic(val -> new AtomicReference<>(val));
        }
        if (is(AtomicStampedReference.class)) {
            return generic(val -> new AtomicStampedReference<>(val, 0));
        }
        if (is(AtomicReferenceArray.class)) {
            return generic(val -> new AtomicReferenceArray<>(new Object[] { val }));
        }
        return Optional.empty();
    }
}
