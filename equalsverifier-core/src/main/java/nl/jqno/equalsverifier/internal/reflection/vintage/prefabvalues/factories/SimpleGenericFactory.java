package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.Func;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider.Attributes;
import nl.jqno.equalsverifier.internal.reflection.vintage.VintageValueProvider;

public class SimpleGenericFactory<T> extends AbstractGenericFactory<T> {

    private final Func<T> factory;
    private final Supplier<T> emptyFactory;

    public SimpleGenericFactory(Func<T> factory, Supplier<T> emptyFactory) {
        this.factory = factory;
        this.emptyFactory = emptyFactory;
    }

    @Override
    public Tuple<T> createValues(
        TypeTag tag,
        VintageValueProvider valueProvider,
        Attributes attributes
    ) {
        Attributes clone = attributes.cloneAndAdd(tag);

        List<Object> redValues = new ArrayList<>();
        List<Object> blueValues = new ArrayList<>();

        boolean useEmpty = false;
        int n = tag.getType().getTypeParameters().length;
        for (int i = 0; i < n; i++) {
            TypeTag paramTag = determineAndCacheActualTypeTag(i, tag, valueProvider, clone);

            Object redValue = valueProvider.giveRed(paramTag, clone);
            Object blueValue = valueProvider.giveBlue(paramTag, clone);
            if (redValue.equals(blueValue)) { // This happens with single-element enums
                useEmpty = true;
            }
            redValues.add(redValue);
            blueValues.add(blueValue);
        }

        Object red = factory.apply(redValues);
        Object blue = useEmpty && emptyFactory != null
            ? emptyFactory.get()
            : factory.apply(blueValues);
        Object redCopy = factory.apply(redValues);

        return Tuple.of(red, blue, redCopy);
    }
}
