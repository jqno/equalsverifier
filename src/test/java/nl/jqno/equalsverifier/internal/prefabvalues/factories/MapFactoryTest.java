/*
 * Copyright 2015-2017 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
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

    private static final MapFactory<Map> MAP_FACTORY = new StubMapPrefabValueFactory();

    private final PrefabValues prefabValues = new PrefabValues();
    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;
    private OneElementEnum redEnum;

    @Before
    public void setUp() {
        JavaApiPrefabValues.addTo(prefabValues);
        red = prefabValues.giveRed(STRING_TYPETAG);
        black = prefabValues.giveBlack(STRING_TYPETAG);
        redObject = prefabValues.giveRed(OBJECT_TYPETAG);
        blackObject = prefabValues.giveBlack(OBJECT_TYPETAG);
        redEnum = prefabValues.giveBlack(ONEELEMENTENUM_TYPETAG);
    }

    @Test
    public void createMapsOfStringToString() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(STRINGSTRINGMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(red, black), tuple.getRed());
        assertEquals(mapOf(black, black), tuple.getBlack());
    }

    @Test
    public void createMapsOfWildcard() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(WILDCARDMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redObject, blackObject), tuple.getRed());
        assertEquals(mapOf(blackObject, blackObject), tuple.getBlack());
    }

    @Test
    public void createRawMaps() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(RAWMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redObject, blackObject), tuple.getRed());
        assertEquals(mapOf(blackObject, blackObject), tuple.getBlack());
    }

    @Test
    public void createMapOfOneElementEnumKey() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(ONEELEMENTENUMKEYMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redEnum, blackObject), tuple.getRed());
        assertEquals(new HashMap<>(), tuple.getBlack());
    }

    private static class StubMapPrefabValueFactory extends MapFactory<Map> {
        @Override
        public Map createEmpty() {
            return new HashMap<>();
        }
    }

    private <K, V> Map<K, V> mapOf(K key, V value) {
        Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return result;
    }
}
