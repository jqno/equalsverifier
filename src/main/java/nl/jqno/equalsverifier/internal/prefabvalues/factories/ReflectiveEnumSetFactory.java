package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumSets
 * using reflection, while taking generics into account.
 */
@SuppressWarnings("rawtypes")
public class ReflectiveEnumSetFactory extends AbstractReflectiveGenericFactory<EnumSet> {
    @Override
    public Tuple<EnumSet> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(EnumSet.class.getName());

        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone, Enum.class);

        EnumSet red = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveRed(entryTag)));
        EnumSet black = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveBlack(entryTag)));
        EnumSet redCopy = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveRed(entryTag)));

        return new Tuple<>(red, black, redCopy);
    }
}
