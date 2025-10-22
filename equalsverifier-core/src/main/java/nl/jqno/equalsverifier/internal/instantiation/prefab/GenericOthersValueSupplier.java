package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinLazy;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;

public class GenericOthersValueSupplier<T> extends GenericValueSupplier<T> {

    public GenericOthersValueSupplier(Class<T> type, ValueProvider vp) {
        super(type, vp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Tuple<T>> get(TypeTag tag, Attributes attributes) {
        if (is(Supplier.class)) {
            return generic(tag, attributes, val -> (T) supplier(val), () -> (T) supplier(null));
        }

        if (is(KotlinScreen.LAZY)) {
            return generic(tag, attributes, val -> (T) KotlinLazy.lazy(val));
        }

        return Optional.empty();
    }

    private <A> Supplier<A> supplier(A val) {
        return () -> val;
    }
}
