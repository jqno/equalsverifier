package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.lang.reflect.Array;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.InstanceCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.ClassAccessor;
import org.objenesis.Objenesis;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates types "by force".
 *
 * <p>It instantiates the type using bytecode magic, bypassing the constructor. Then it uses {@link
 * VintageValueProvider} to fill up all the fields, recursively.
 */
public class FallbackFactory<T> implements PrefabValueFactory<T> {

    private final Objenesis objenesis;

    public FallbackFactory(Objenesis objenesis) {
        this.objenesis = objenesis;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        Attributes clone = attributes.cloneAndAdd(tag);

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

        switch (enumConstants.length) {
            case 0:
                return new Tuple<>(null, null, null);
            case 1:
                return new Tuple<>(enumConstants[0], enumConstants[0], enumConstants[0]);
            default:
                return new Tuple<>(enumConstants[0], enumConstants[1], enumConstants[0]);
        }
    }

    @SuppressWarnings("unchecked")
    private Tuple<T> giveArrayInstances(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        Class<T> type = tag.getType();
        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);

        T red = (T) Array.newInstance(componentType, 1);
        Array.set(red, 0, valueProvider.giveRed(componentTag, attributes));
        T blue = (T) Array.newInstance(componentType, 1);
        Array.set(blue, 0, valueProvider.giveBlue(componentTag, attributes));
        T redCopy = (T) Array.newInstance(componentType, 1);
        Array.set(redCopy, 0, valueProvider.giveRed(componentTag, attributes));

        return new Tuple<>(red, blue, redCopy);
    }

    private Tuple<T> giveInstances(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        ClassAccessor<T> accessor = new ClassAccessor<>(tag.getType(), valueProvider, objenesis);
        T red = accessor.getRedObject(tag, attributes);
        T blue = accessor.getBlueObject(tag, attributes);

        @SuppressWarnings("unchecked")
        Class<T> actualType = (Class<T>) red.getClass();
        T redCopy = new InstanceCreator<>(new ClassProbe<T>(actualType), objenesis).copy(red);

        return new Tuple<>(red, blue, redCopy);
    }
}
