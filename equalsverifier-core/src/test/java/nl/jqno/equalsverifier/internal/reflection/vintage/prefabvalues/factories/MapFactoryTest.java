package nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.EmptyValueProvider;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

@SuppressWarnings("rawtypes")
public class MapFactoryTest {

    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG = new TypeTag(
        Map.class,
        STRING_TYPETAG,
        STRING_TYPETAG
    );
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDMAP_TYPETAG = new TypeTag(
        Map.class,
        OBJECT_TYPETAG,
        OBJECT_TYPETAG
    );
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);
    private static final TypeTag ONEELEMENTENUM_TYPETAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag ONEELEMENTENUMKEYMAP_TYPETAG = new TypeTag(
        Map.class,
        ONEELEMENTENUM_TYPETAG,
        OBJECT_TYPETAG
    );

    private static final MapFactory<Map> MAP_FACTORY = new MapFactory<>(HashMap::new);

    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private VintageValueProvider valueProvider;
    private String red;
    private String blue;
    private Object redObject;
    private Object blueObject;
    private OneElementEnum redEnum;

    @BeforeEach
    public void setUp() {
        valueProvider =
            new VintageValueProvider(
                EmptyValueProvider.INSTANCE,
                JavaApiPrefabValues.build(),
                new ObjenesisStd()
            );
        red = valueProvider.giveRed(STRING_TYPETAG);
        blue = valueProvider.giveBlue(STRING_TYPETAG);
        redObject = valueProvider.giveRed(OBJECT_TYPETAG);
        blueObject = valueProvider.giveBlue(OBJECT_TYPETAG);
        redEnum = valueProvider.giveBlue(ONEELEMENTENUM_TYPETAG);
    }

    @Test
    public void createMapsOfStringToString() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(
            STRINGSTRINGMAP_TYPETAG,
            valueProvider,
            typeStack
        );
        assertEquals(mapOf(red, blue), tuple.getRed());
        assertEquals(mapOf(blue, blue), tuple.getBlue());
    }

    @Test
    public void createMapsOfWildcard() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(WILDCARDMAP_TYPETAG, valueProvider, typeStack);
        assertEquals(mapOf(redObject, blueObject), tuple.getRed());
        assertEquals(mapOf(blueObject, blueObject), tuple.getBlue());
    }

    @Test
    public void createRawMaps() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(RAWMAP_TYPETAG, valueProvider, typeStack);
        assertEquals(mapOf(redObject, blueObject), tuple.getRed());
        assertEquals(mapOf(blueObject, blueObject), tuple.getBlue());
    }

    @Test
    public void createMapOfOneElementEnumKey() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(
            ONEELEMENTENUMKEYMAP_TYPETAG,
            valueProvider,
            typeStack
        );
        assertEquals(mapOf(redEnum, blueObject), tuple.getRed());
        assertEquals(new HashMap<>(), tuple.getBlue());
    }

    private <K, V> Map<K, V> mapOf(K key, V value) {
        Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return result;
    }
}
