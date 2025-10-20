package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Optional<Tuple<T>> collection(
            TypeTag tag,
            Attributes attributes,
            Supplier<? extends Collection> construct) {
        var elements = vp.provideOrThrow(tag.genericTypes().get(0), attributes.clearName());

        var red = construct.get();
        red.add(elements.red());

        var blue = construct.get();
        if (!elements.red().equals(elements.blue())) { // This happens with single-element enums
            blue.add(elements.blue());
        }

        var redCopy = construct.get();
        redCopy.add(elements.redCopy());

        return Optional.of(new Tuple<>((T) red, (T) blue, (T) redCopy));
    }
}
