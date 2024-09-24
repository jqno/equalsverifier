package nl.jqno.equalsverifier.internal.reflection.vintage;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import org.objenesis.Objenesis;

/**
 * Implementation of ObjectAccessor that modifies its wrapped object in-place through reflection.
 *
 * @param <T> The specified object's class.
 */
final class InPlaceObjectAccessor<T> extends ObjectAccessor<T> {

    /** Package-private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */InPlaceObjectAccessor(T object, Class<T> type) {
        super(object, type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy(Objenesis objenesis) {
        T copy = Instantiator.of(type(), objenesis).instantiate();
        for (Field field : FieldIterable.of(type())) {
            fieldModifierFor(field).copyTo(copy);
        }
        return copy;
    }

    /** {@inheritDoc} */
    @Override
    public ObjectAccessor<T> scramble(
        VintageValueProvider valueProvider,
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        for (Field field : FieldIterable.of(type())) {
            fieldModifierFor(field).changeField(valueProvider, enclosingType, typeStack);
        }
        return this;
    }

    private FieldModifier fieldModifierFor(Field field) {
        return FieldModifier.of(field, get());
    }
}
