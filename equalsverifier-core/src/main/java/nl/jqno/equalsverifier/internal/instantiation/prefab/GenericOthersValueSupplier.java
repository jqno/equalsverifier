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

    public GenericOthersValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        super(tag, vp, attributes);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(Supplier.class)) {
            return generic(val -> supplier(val), () -> supplier(null));
        }

        if (is(KotlinScreen.LAZY)) {
            return generic(val -> KotlinLazy.lazy(val));
        }

        return Optional.empty();
    }

    private <A> Supplier<A> supplier(A val) {
        return () -> val;
    }
}
