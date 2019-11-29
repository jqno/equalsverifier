package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates types "by force".
 *
 * <p>It instantiates the type using bytecode magic, bypassing the constructor. Then it uses {@link
 * PrefabValues} to fill up all the fields, recursively.
 */
public class FallbackFactory<T> implements PrefabValueFactory<T> {
    @Override
    public Tuple<T> createValues(
            TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>) typeStack.clone();
        clone.add(tag);

        Class<T> type = tag.getType();
        if (type.isEnum()) {
            return giveEnumInstances(tag);
        }
        if (type.isArray()) {
            return giveArrayInstances(tag, prefabValues, clone);
        }

        traverseFields(tag, prefabValues, clone);
        return giveInstances(tag, prefabValues);
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
            TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        Class<T> type = tag.getType();
        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);
        prefabValues.realizeCacheFor(componentTag, typeStack);

        T red = (T) Array.newInstance(componentType, 1);
        Array.set(red, 0, prefabValues.giveRed(componentTag));
        T black = (T) Array.newInstance(componentType, 1);
        Array.set(black, 0, prefabValues.giveBlack(componentTag));
        T redCopy = (T) Array.newInstance(componentType, 1);
        Array.set(redCopy, 0, prefabValues.giveRed(componentTag));

        return new Tuple<>(red, black, redCopy);
    }

    private void traverseFields(
            TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        Class<?> type = tag.getType();
        for (Field field : FieldIterable.of(type)) {
            int modifiers = field.getModifiers();
            boolean isStaticAndFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
            if (!isStaticAndFinal) {
                prefabValues.realizeCacheFor(TypeTag.of(field, tag), typeStack);
            }
        }
    }

    private Tuple<T> giveInstances(TypeTag tag, PrefabValues prefabValues) {
        ClassAccessor<T> accessor = ClassAccessor.of(tag.getType(), prefabValues);
        T red = accessor.getRedObject(tag);
        T black = accessor.getBlackObject(tag);
        T redCopy = accessor.getRedObject(tag);
        return new Tuple<>(red, black, redCopy);
    }
}
