package nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import nl.jqno.equalsverifier.internal.instantiation.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;

@SuppressWarnings({ "rawtypes", "unchecked" })
class MapFactoryTest {

    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG = new TypeTag(Map.class, STRING_TYPETAG, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDMAP_TYPETAG = new TypeTag(Map.class, OBJECT_TYPETAG, OBJECT_TYPETAG);
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);
    private static final TypeTag ONEELEMENTENUM_TYPETAG = new TypeTag(OneElementEnum.class);
    private static final TypeTag ONEELEMENTENUMKEYMAP_TYPETAG =
            new TypeTag(Map.class, ONEELEMENTENUM_TYPETAG, OBJECT_TYPETAG);

    private static final MapFactory<Map> MAP_FACTORY = new MapFactory<>(HashMap::new);

    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private VintageValueProvider valueProvider;
    private String red;
    private String blue;
    private Object redObject;
    private Object blueObject;
    private OneElementEnum redEnum;

    @BeforeEach
    void setUp() {
        valueProvider = new VintageValueProvider(JavaApiPrefabValues.build(), new ObjenesisStd());
        red = valueProvider.giveRed(STRING_TYPETAG);
        blue = valueProvider.giveBlue(STRING_TYPETAG);
        redObject = valueProvider.giveRed(OBJECT_TYPETAG);
        blueObject = valueProvider.giveBlue(OBJECT_TYPETAG);
        redEnum = valueProvider.giveBlue(ONEELEMENTENUM_TYPETAG);
    }

    @Test
    void createMapsOfStringToString() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(STRINGSTRINGMAP_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.getRed()).isEqualTo(Map.of(red, blue));
        assertThat(tuple.getBlue()).isEqualTo(Map.of(blue, blue));
    }

    @Test
    void createMapsOfWildcard() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(WILDCARDMAP_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.getRed()).isEqualTo(Map.of(redObject, blueObject));
        assertThat(tuple.getBlue()).isEqualTo(Map.of(blueObject, blueObject));
    }

    @Test
    void createRawMaps() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(RAWMAP_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.getRed()).isEqualTo(Map.of(redObject, blueObject));
        assertThat(tuple.getBlue()).isEqualTo(Map.of(blueObject, blueObject));
    }

    @Test
    void createMapOfOneElementEnumKey() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(ONEELEMENTENUMKEYMAP_TYPETAG, valueProvider, typeStack);
        assertThat(tuple.getRed()).isEqualTo(Map.of(redEnum, blueObject));
        assertThat(tuple.getBlue()).isEqualTo(Map.of());
    }
}
