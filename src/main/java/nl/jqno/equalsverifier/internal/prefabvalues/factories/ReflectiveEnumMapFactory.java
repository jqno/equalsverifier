package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumMaps
 * using reflection, while taking generics into account.
 */
@SuppressWarnings("rawtypes")
public class ReflectiveEnumMapFactory extends AbstractReflectiveGenericFactory<EnumMap> {
    @Override
    public Tuple<EnumMap> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);

        EnumMap red = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        EnumMap black = createWith(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));
        EnumMap redCopy = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));

        return new Tuple<>(red, black, redCopy);
    }

    @SuppressWarnings("unchecked")
    private EnumMap createWith(Object key, Object value) {
        Map result = new HashMap();
        invoke(Map.class, result, "put", classes(Object.class, Object.class), objects(key, value));
        return new EnumMap<>(result);
    }
}
