package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

public class ReflectiveLazyAwtFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    public ReflectiveLazyAwtFactory(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        String colorSpace = "java.awt.color.ColorSpace";
        ConditionalInstantiator ciColorSpace = new ConditionalInstantiator(colorSpace);
        Object redConstant = ciColorSpace.returnConstant("CS_sRGB");
        Object blackConstant = ciColorSpace.returnConstant("CS_LINEAR_RGB");

        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        Object red = ci.callFactory("getInstance", classes(int.class), objects(redConstant));
        Object black = ci.callFactory("getInstance", classes(int.class), objects(blackConstant));
        Object redCopy = ci.callFactory("getInstance", classes(int.class), objects(redConstant));

        return Tuple.of(red, black, redCopy);
    }
}
