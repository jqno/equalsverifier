package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

import nl.jqno.equalsverifier.Func;
import nl.jqno.equalsverifier.internal.instantiation.Attributes;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

public class SimpleGenericFactory<T> extends AbstractGenericFactory<T> {

    private final Func<T> factory;
    private final Supplier<T> emptyFactory;

    public SimpleGenericFactory(Func<T> factory, Supplier<T> emptyFactory) {
        this.factory = factory;
        this.emptyFactory = emptyFactory;
    }

    @Override
    @SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
    public Tuple<T> createValues(TypeTag tag, VintageValueProvider valueProvider, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);

        var redValues = new ArrayList<Object>();
        var blueValues = new ArrayList<Object>();

        boolean useEmpty = false;
        int n = tag.getType().getTypeParameters().length;
        for (int i = 0; i < n; i++) {
            TypeTag paramTag = determineAndCacheActualTypeTag(i, tag, valueProvider, clone);

            Tuple<?> tuple = valueProvider.provideOrThrow(paramTag, Attributes.empty());
            Object redValue = tuple.red();
            Object blueValue = tuple.blue();
            if (redValue.equals(blueValue)) { // This happens with single-element enums
                useEmpty = true;
            }
            redValues.add(redValue);
            blueValues.add(blueValue);
        }

        T red = factory.apply(redValues);
        T blue = useEmpty && emptyFactory != null ? emptyFactory.get() : factory.apply(blueValues);
        T redCopy = factory.apply(redValues);

        return new Tuple<>(red, blue, redCopy);
    }
}
