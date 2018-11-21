package equalsverifier.prefabvalues.factories;

import equalsverifier.prefabvalues.JavaApiPrefabValues;
import equalsverifier.prefabservice.PrefabAbstract;
import equalsverifier.prefabvalues.PrefabValues;
import equalsverifier.prefabservice.Tuple;
import equalsverifier.gentype.TypeTag;
import equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class MapFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG = new TypeTag(Map.class, STRING_TYPETAG, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDMAP_TYPETAG = new TypeTag(Map.class, OBJECT_TYPETAG, OBJECT_TYPETAG);
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);
    private static final TypeTag ONEELEMENTENUM_TYPETAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag ONEELEMENTENUMKEYMAP_TYPETAG = new TypeTag(Map.class, ONEELEMENTENUM_TYPETAG, OBJECT_TYPETAG);

    private static final MapFactory<Map> MAP_FACTORY = new MapFactory<>(HashMap::new);

    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private PrefabAbstract prefabAbstract;
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;
    private OneElementEnum redEnum;

    @Before
    public void setUp() {
        prefabAbstract = new PrefabValues(JavaApiPrefabValues.build());
        red = prefabAbstract.giveRed(STRING_TYPETAG);
        black = prefabAbstract.giveBlack(STRING_TYPETAG);
        redObject = prefabAbstract.giveRed(OBJECT_TYPETAG);
        blackObject = prefabAbstract.giveBlack(OBJECT_TYPETAG);
        redEnum = prefabAbstract.giveBlack(ONEELEMENTENUM_TYPETAG);
    }

    @Test
    public void createMapsOfStringToString() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(STRINGSTRINGMAP_TYPETAG, prefabAbstract, typeStack);
        assertEquals(mapOf(red, black), tuple.getRed());
        assertEquals(mapOf(black, black), tuple.getBlack());
    }

    @Test
    public void createMapsOfWildcard() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(WILDCARDMAP_TYPETAG, prefabAbstract, typeStack);
        assertEquals(mapOf(redObject, blackObject), tuple.getRed());
        assertEquals(mapOf(blackObject, blackObject), tuple.getBlack());
    }

    @Test
    public void createRawMaps() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(RAWMAP_TYPETAG, prefabAbstract, typeStack);
        assertEquals(mapOf(redObject, blackObject), tuple.getRed());
        assertEquals(mapOf(blackObject, blackObject), tuple.getBlack());
    }

    @Test
    public void createMapOfOneElementEnumKey() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(ONEELEMENTENUMKEYMAP_TYPETAG, prefabAbstract, typeStack);
        assertEquals(mapOf(redEnum, blackObject), tuple.getRed());
        assertEquals(new HashMap<>(), tuple.getBlack());
    }

    private <K, V> Map<K, V> mapOf(K key, V value) {
        Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return result;
    }
}
