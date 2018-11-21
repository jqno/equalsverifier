package equalsverifier.prefabvalues.factories;

import equalsverifier.gentype.TypeTag;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabvalues.PrefabValues;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.reflection.ClassAccessor;
import equalsverifier.reflection.FieldIterable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates types
 * "by force".
 *
 * It instantiates the type using bytecode magic, bypassing the constructor.
 * Then it uses {@link PrefabValues} to fill up all the fields, recursively.
 */
public class FallbackFactory<T> implements PrefabValueFactory<T> {
    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>)typeStack.clone();
        clone.add(tag);

        Class<T> type = tag.getType();
        if (type.isEnum()) {
            return giveEnumInstances(tag);
        }
        if (type.isArray()) {
            return giveArrayInstances(tag, prefabAbstract, clone);
        }

        traverseFields(tag, prefabAbstract, clone);
        return giveInstances(tag, prefabAbstract);
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
    private Tuple<T> giveArrayInstances(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        Class<T> type = tag.getType();
        Class<?> componentType = type.getComponentType();
        TypeTag componentTag = new TypeTag(componentType);
        prefabAbstract.realizeCacheFor(componentTag, typeStack);

        T red = (T)Array.newInstance(componentType, 1);
        Array.set(red, 0, prefabAbstract.giveRed(componentTag));
        T black = (T)Array.newInstance(componentType, 1);
        Array.set(black, 0, prefabAbstract.giveBlack(componentTag));
        T redCopy = (T)Array.newInstance(componentType, 1);
        Array.set(redCopy, 0, prefabAbstract.giveRed(componentTag));

        return new Tuple<>(red, black, redCopy);
    }

    private void traverseFields(TypeTag tag, PrefabAbstract prefabAbstract, LinkedHashSet<TypeTag> typeStack) {
        Class<?> type = tag.getType();
        for (Field field : FieldIterable.of(type)) {
            int modifiers = field.getModifiers();
            boolean isStaticAndFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
            if (!isStaticAndFinal) {
                prefabAbstract.realizeCacheFor(TypeTag.of(field, tag), typeStack);
            }
        }
    }

    private Tuple<T> giveInstances(TypeTag tag, PrefabAbstract prefabAbstract) {
        ClassAccessor<T> accessor = ClassAccessor.of(tag.getType(), prefabAbstract);
        T red = accessor.getRedObject(tag);
        T black = accessor.getBlackObject(tag);
        T redCopy = accessor.getRedObject(tag);
        return new Tuple<>(red, black, redCopy);
    }
}
