package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates objects using
 * reflection, while taking generics into account.
 */
public class ReflectiveGenericContainerFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;
    private final String factoryMethod;
    private final Class<?> parameterType;

    public ReflectiveGenericContainerFactory(String typeName, String factoryMethod, final Class<?> parameterType) {
        this.typeName = typeName;
        this.factoryMethod = factoryMethod;
        this.parameterType = parameterType;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        TypeTag internalTag = determineActualTypeTagFor(0, tag);

        Object red = createWith(prefabValues.giveRed(internalTag));
        Object black = createWith(prefabValues.giveBlack(internalTag));
        Object redCopy = createWith(prefabValues.giveRed(internalTag));

        return Tuple.of(red, black, redCopy);
    }

    @SuppressWarnings("rawtypes")
    private Object createWith(Object value) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        return ci.callFactory(factoryMethod, classes(parameterType), objects(value));
    }
}
