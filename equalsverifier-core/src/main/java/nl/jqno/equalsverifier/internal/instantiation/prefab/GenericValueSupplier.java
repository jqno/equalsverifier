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

    /*
     * T is an unknown type at compile-time, but the types of the three parameters are known at compile-time. The
     * compiler thinks these are not the same as T, even though we know they are. Therefore, we must trick the compiler
     * by introducing type parameter S and casting the values.
     */
    @SuppressWarnings("unchecked")
    protected <S> Optional<Tuple<T>> val(S red, S blue, S redCopy) {
        return Optional.of(new Tuple<>((T) red, (T) blue, (T) redCopy));
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
                .map(val -> (T) construct.supply(val))
                .swapBlueIfEqualToRed(() -> (T) empty.get());
        return Optional.of(tup);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Optional<Tuple<T>> collection(Supplier<? extends Collection> construct) {
        var tup = vp.provideOrThrow(determineGenericType(0), attributes.clearName()).map(e -> {
            var coll = construct.get();
            coll.add(e);
            return (T) coll;
        }).swapBlueIfEqualToRed(() -> (T) construct.get());
        return Optional.of(tup);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Optional<Tuple<T>> map(Supplier<Map> construct) {
        var keys = vp.provideOrThrow(determineGenericType(0), attributes.clearName());
        var values = vp.provideOrThrow(determineGenericType(1), attributes.clearName());

        // Use red for key and blue for value in the Red map to avoid having identical keys and values.
        // But don't do it in the Blue map, or they may cancel each other out again.

        var tup = keys.map(e -> {
            var map = construct.get();
            map.put(e, values.blue());
            return (T) map;
        }).swapBlueIfEqualToRed(() -> (T) construct.get());

        return Optional.of(tup);
    }

    private TypeTag determineGenericType(int index) {
        if (tag.genericTypes().size() <= index) {
            return OBJECT;
        }
        return tag.genericTypes().get(index);
    }
}
