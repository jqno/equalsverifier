package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.Before;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
public class MapFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG =
            new TypeTag(Map.class, STRING_TYPETAG, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDMAP_TYPETAG =
            new TypeTag(Map.class, OBJECT_TYPETAG, OBJECT_TYPETAG);
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);
    private static final TypeTag ONEELEMENTENUM_TYPETAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag ONEELEMENTENUMKEYMAP_TYPETAG =
            new TypeTag(Map.class, ONEELEMENTENUM_TYPETAG, OBJECT_TYPETAG);

    private static final MapFactory<Map> MAP_FACTORY = new MapFactory<>(HashMap::new);

    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private PrefabValues prefabValues;
    private String red;
    private String blue;
    private Object redObject;
    private Object blueObject;
    private OneElementEnum redEnum;

    @Before
    public void setUp() {
        prefabValues = new PrefabValues(JavaApiPrefabValues.build());
        red = prefabValues.giveRed(STRING_TYPETAG);
        blue = prefabValues.giveBlue(STRING_TYPETAG);
        redObject = prefabValues.giveRed(OBJECT_TYPETAG);
        blueObject = prefabValues.giveBlue(OBJECT_TYPETAG);
        redEnum = prefabValues.giveBlue(ONEELEMENTENUM_TYPETAG);
    }

    @Test
    public void createMapsOfStringToString() {
        Tuple<Map> tuple =
                MAP_FACTORY.createValues(STRINGSTRINGMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(red, blue), tuple.getRed());
        assertEquals(mapOf(blue, blue), tuple.getBlue());
    }

    @Test
    public void createMapsOfWildcard() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(WILDCARDMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redObject, blueObject), tuple.getRed());
        assertEquals(mapOf(blueObject, blueObject), tuple.getBlue());
    }

    @Test
    public void createRawMaps() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(RAWMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redObject, blueObject), tuple.getRed());
        assertEquals(mapOf(blueObject, blueObject), tuple.getBlue());
    }

    @Test
    public void createMapOfOneElementEnumKey() {
        Tuple<Map> tuple =
                MAP_FACTORY.createValues(ONEELEMENTENUMKEYMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redEnum, blueObject), tuple.getRed());
        assertEquals(new HashMap<>(), tuple.getBlue());
    }

    private <K, V> Map<K, V> mapOf(K key, V value) {
        Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return result;
    }
}
