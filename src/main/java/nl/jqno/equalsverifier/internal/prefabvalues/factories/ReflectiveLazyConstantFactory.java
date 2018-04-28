package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

public class ReflectiveLazyConstantFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;
    private final String redConstant;
    private final String blackConstant;

    public ReflectiveLazyConstantFactory(String typeName, String redConstant, String blackConstant) {
        this.typeName = typeName;
        this.redConstant = redConstant;
        this.blackConstant = blackConstant;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);

        Object red = ci.returnConstant(redConstant);
        Object black = ci.returnConstant(blackConstant);
        Object redCopy = ci.returnConstant(redConstant);

        return Tuple.of(red, black, redCopy);
    }
}
