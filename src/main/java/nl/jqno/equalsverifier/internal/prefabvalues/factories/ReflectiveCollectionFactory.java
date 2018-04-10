package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.*;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates collections
 * using reflection, while taking generics into account.
 */
public abstract class ReflectiveCollectionFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    /* default */ ReflectiveCollectionFactory(String typeName) {
        this.typeName = typeName;
    }

    public static <T> ReflectiveCollectionFactory<T> callFactoryMethod(final String typeName, final String methodName) {
        return new ReflectiveCollectionFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(), objects());
            }
        };
    }

    public static <T> ReflectiveCollectionFactory<T> callFactoryMethodWithParameter(
            final String typeName, final String methodName, final Class<?> parameterType, final Object parameterValue) {
        return new ReflectiveCollectionFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(parameterType), objects(parameterValue));
            }
        };
    }

    protected String getTypeName() {
        return typeName;
    }

    protected abstract Object createEmpty();

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);

        Object red = createWith(prefabValues.giveRed(entryTag));
        Object black = createWith(prefabValues.giveBlack(entryTag));
        Object redCopy = createWith(prefabValues.giveRed(entryTag));

        return Tuple.of(red, black, redCopy);
    }

    private Object createWith(Object value) {
        Object result = createEmpty();
        invoke(classForName(typeName), result, "add", classes(Object.class), objects(value));
        return result;
    }
}
