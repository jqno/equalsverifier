package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.lang.reflect.Array;
import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import org.objenesis.Objenesis;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates types "by force".
 *
 * <p>
 * It instantiates the type using bytecode magic, bypassing the constructor. Then it uses {@link VintageValueProvider}
 * to fill up all the fields, recursively.
 */
@SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
public class FallbackFactory<T> implements PrefabValueFactory<T> {

    private final Objenesis objenesis;

    public FallbackFactory(Objenesis objenesis) {
        this.objenesis = objenesis;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>) typeStack.clone();
        clone.add(tag);

        Class<T> type = tag.getType();
        if (type.isEnum()) {
            return giveEnumInstances(tag);
        }
        if (type.isArray()) {
            return giveArrayInstances(tag, valueProvider, clone);
        }

        return giveInstances(tag, valueProvider, clone);
    }

    private Tuple<T> giveEnumInstances(TypeTag tag) {
        Class<T> type = tag.getType();
        T[] enumConstants = type.getEnumConstants();

        return switch (enumConstants.length) {
            case 0 -> new Tuple<>(null, null, null);
            case 1 -> new Tuple<>(enumConstants[0], enumConstants[0], enumConstants[0]);
            default -> new Tuple<>(enumConstants[0], enumConstants[1], enumConstants[0]);
        };
    }

    @SuppressWarnings("unchecked")
    private Tuple<T> giveArrayInstances(
            TypeTag tag,
            VintageValueProvider valueProvider,
            LinkedHashSet<TypeTag> typeStack) {
        Class<T> type = tag.getType();
        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);
        valueProvider.realizeCacheFor(componentTag, typeStack);

        Tuple<?> tuple = valueProvider.provideOrThrow(componentTag, Attributes.empty());
        T red = (T) Array.newInstance(componentType, 1);
        Array.set(red, 0, tuple.red());
        T blue = (T) Array.newInstance(componentType, 2);
        Array.set(blue, 0, tuple.blue());
        Array.set(blue, 1, tuple.red());
        T redCopy = (T) Array.newInstance(componentType, 1);
        Array.set(redCopy, 0, tuple.red());

        return new Tuple<>(red, blue, redCopy);
    }

    private Tuple<T> giveInstances(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        ClassAccessor<T> accessor = ClassAccessor.of(tag.getType(), valueProvider, objenesis);
        T red = accessor.getRedObject(tag, typeStack);
        T blue = accessor.getBlueObject(tag, typeStack);
        T redCopy = accessor.getRedObject(tag, typeStack);
        return new Tuple<>(red, blue, redCopy);
    }
}
