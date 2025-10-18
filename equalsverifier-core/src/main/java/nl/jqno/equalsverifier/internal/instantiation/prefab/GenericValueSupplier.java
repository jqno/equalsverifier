package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Optional;
import java.util.function.Function;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public abstract class GenericValueSupplier<T> {
    private final Class<T> type;
    private final ValueProvider vp;

    public GenericValueSupplier(Class<T> type, ValueProvider vp) {
        this.type = type;
        this.vp = vp;
    }

    public abstract Optional<Tuple<T>> get(TypeTag tag, Attributes attributes);

    protected boolean is(Class<?> otherType) {
        return type.equals(otherType);
    }

    protected Optional<Tuple<T>> valGeneric1(TypeTag tag, Attributes attributes, Function<Object, T> construct) {
        return vp.provide(tag.genericTypes().get(0), attributes.clearName()).map(tup -> tup.map(construct));
    }
}
