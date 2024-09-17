package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.ClassAccessor;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates types "by force".
 *
 * <p>It instantiates the type using bytecode magic, bypassing the constructor. Then it uses {@link
 * VintageValueProvider} to fill up all the fields, recursively.
 */
public class FallbackFactory<T> implements PrefabValueFactory<T> {

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        LinkedHashSet<TypeTag> typeStack
    ) {
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

        traverseFields(tag, valueProvider, clone);
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
        LinkedHashSet<TypeTag> typeStack
    ) {
        Class<T> type = tag.getType();
        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);
        valueProvider.realizeCacheFor(componentTag, typeStack);

        T red = (T) Array.newInstance(componentType, 1);
        Array.set(red, 0, valueProvider.giveRed(componentTag));
        T blue = (T) Array.newInstance(componentType, 1);
        Array.set(blue, 0, valueProvider.giveBlue(componentTag));
        T redCopy = (T) Array.newInstance(componentType, 1);
        Array.set(redCopy, 0, valueProvider.giveRed(componentTag));

        return new Tuple<>(red, blue, redCopy);
    }

    private void traverseFields(
        TypeTag tag,
        VintageValueProvider valueProvider,
        LinkedHashSet<TypeTag> typeStack
    ) {
        Class<?> type = tag.getType();
        for (Field field : FieldIterable.of(type)) {
            FieldProbe probe = FieldProbe.of(field);
            boolean isStaticAndFinal = probe.isStatic() && probe.isFinal();
            if (!isStaticAndFinal) {
                valueProvider.realizeCacheFor(TypeTag.of(field, tag), typeStack);
            }
        }
    }

    private Tuple<T> giveInstances(
        TypeTag tag,
        VintageValueProvider valueProvider,
        LinkedHashSet<TypeTag> typeStack
    ) {
        ClassAccessor<T> accessor = ClassAccessor.of(tag.getType(), valueProvider);
        T red = accessor.getRedObject(tag, typeStack);
        T blue = accessor.getBlueObject(tag, typeStack);
        T redCopy = accessor.getRedObject(tag, typeStack);
        return new Tuple<>(red, blue, redCopy);
    }
}
