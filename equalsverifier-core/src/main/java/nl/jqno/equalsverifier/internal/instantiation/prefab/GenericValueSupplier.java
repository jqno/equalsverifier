package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.Func.Func1;
import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public abstract class GenericValueSupplier<T> {

    private static final TypeTag OBJECT = new TypeTag(Object.class);

    private final TypeTag tag;
    private final ValueProvider vp;
    private final Attributes attributes;

    public GenericValueSupplier(TypeTag tag, ValueProvider vp, Attributes attributes) {
        this.tag = tag;
        this.vp = vp;
        this.attributes = attributes;
    }

    public abstract Optional<Tuple<T>> get();

    protected boolean is(Class<?> otherType) {
        return tag.getType().equals(otherType);
    }

    @SuppressWarnings("unchecked")
    protected Optional<Tuple<T>> generic(Func1<Object, ?> construct) {
        var tup = vp
                .provideOrThrow(determineGenericType(0), attributes.clearName())
                .map(val -> (T) construct.supply(val));
        return Optional.of(tup);
    }

    @SuppressWarnings("unchecked")
    protected Optional<Tuple<T>> generic(Func1<Object, ?> construct, Supplier<?> empty) {
        var tup = vp
                .provideOrThrow(determineGenericType(0), attributes.clearName())
                .map(val -> (T) construct.supply(val));
        if (tup.red().equals(tup.blue())) {
            return Optional.of(new Tuple<T>(tup.red(), (T) empty.get(), tup.redCopy()));
        }
        return Optional.of(tup);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Optional<Tuple<T>> collection(Supplier<? extends Collection> construct) {
        var elements = vp.provideOrThrow(determineGenericType(0), attributes.clearName());

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
    protected Optional<Tuple<T>> map(Supplier<Map> construct) {
        var keys = vp.provideOrThrow(determineGenericType(0), attributes.clearName());
        var values = vp.provideOrThrow(determineGenericType(1), attributes.clearName());

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

    private TypeTag determineGenericType(int index) {
        if (tag.genericTypes().size() <= index) {
            return OBJECT;
        }
        return tag.genericTypes().get(index);
    }
}
