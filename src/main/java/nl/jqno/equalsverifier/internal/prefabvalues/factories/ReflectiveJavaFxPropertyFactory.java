package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that specializes in JavaFX
 * property classes, taking generics into account.
 */
public final class ReflectiveJavaFxPropertyFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;
    private final Class<?> parameterRawType;

    public ReflectiveJavaFxPropertyFactory(String typeName, Class<?> parameterRawType) {
        this.typeName = typeName;
        this.parameterRawType = parameterRawType;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        TypeTag singleParameterTag = copyGenericTypesInto(parameterRawType, tag);
        Object red = ci.instantiate(classes(parameterRawType), objects(prefabValues.giveRed(singleParameterTag)));
        Object black = ci.instantiate(classes(parameterRawType), objects(prefabValues.giveBlack(singleParameterTag)));
        Object redCopy = ci.instantiate(classes(parameterRawType), objects(prefabValues.giveRed(singleParameterTag)));

        return Tuple.of(red, black, redCopy);
    }
}
