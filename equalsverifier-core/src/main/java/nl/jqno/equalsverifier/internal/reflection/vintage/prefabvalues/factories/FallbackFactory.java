package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.reflection.*;
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
        LinkedHashSet<TypeTag> typeStack
    ) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>) typeStack.clone();
        clone.add(tag);

        traverseFields(tag, valueProvider, clone);
        return giveInstances(tag, valueProvider, clone);
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
        ClassAccessor<T> accessor = ClassAccessor.of(tag.getType(), valueProvider, objenesis);
        T red = accessor.getRedObject(tag, typeStack);
        T blue = accessor.getBlueObject(tag, typeStack);
        T redCopy = accessor.getRedObject(tag, typeStack);
        return new Tuple<>(red, blue, redCopy);
    }
}
