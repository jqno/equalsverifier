package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.LinkedHashSet;

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

        return giveInstances(tag, valueProvider, clone);
    }

    private Tuple<T> giveInstances(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        ClassAccessor<T> accessor = ClassAccessor.of(tag.getType(), valueProvider, objenesis);
        T red = accessor.getRedObject(tag, typeStack);
        T blue = accessor.getBlueObject(tag, typeStack);
        T redCopy = accessor.getRedObject(tag, typeStack);
        return new Tuple<>(red, blue, redCopy);
    }
}
