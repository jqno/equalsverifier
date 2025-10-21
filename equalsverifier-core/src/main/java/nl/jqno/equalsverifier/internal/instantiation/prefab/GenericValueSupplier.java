package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Collection;
import java.util.Map;
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Optional<Tuple<T>> map(TypeTag tag, Attributes attributes, Supplier<Map> construct) {
        var keys = vp.provideOrThrow(tag.genericTypes().get(0), attributes.clearName());
        var values = vp.provideOrThrow(tag.genericTypes().get(1), attributes.clearName());

        // Use red for key and blue for value in the Red map to avoid having identical keys and values.
        // But don't do it in the Blue map, or they may cancel each other out again.

        var redKey = keys.red();
        var blueKey = keys.blue();
        var blueValue = values.blue();

        var red = construct.get();
        red.put(redKey, blueValue);

        var blue = construct.get();
        if (!redKey.equals(blueKey)) { // This happens with single-element enums
            blue.put(blueKey, blueValue);
        }

        var redCopy = construct.get();
        redCopy.put(redKey, blueValue);

        return Optional.of(new Tuple<>((T) red, (T) blue, (T) redCopy));
    }
}
