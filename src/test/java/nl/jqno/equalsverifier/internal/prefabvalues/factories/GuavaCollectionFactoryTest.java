/*
 * Copyright 2015 Jan Ouwens
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import nl.jqno.equalsverifier.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.GuavaCollectionFactory.Kind.COLLECTION;
import static nl.jqno.equalsverifier.internal.prefabvalues.factories.GuavaCollectionFactory.Kind.MAP;
import static org.junit.Assert.assertEquals;

public class GuavaCollectionFactoryTest {
    private PrefabValues prefabValues;

    @Before
    public void setUp() {
        prefabValues = new PrefabValues(new StaticFieldValueStash());
        JavaApiPrefabValues.addTo(prefabValues);
    }

    @Test
    public void createInstancesWithCorrectSingleGenericParameter() {
        TypeTag tag = new TypeTag(ImmutableList.class, new TypeTag(String.class));
        TypeTag listTag = new TypeTag(List.class, new TypeTag(String.class));

        PrefabValueFactory<ImmutableList> factory = new GuavaCollectionFactory<>("ImmutableList", COLLECTION);
        Tuple<ImmutableList> tuple = factory.createValues(tag, prefabValues, null);

        assertEquals(prefabValues.giveRed(listTag), tuple.getRed());
        assertEquals(prefabValues.giveBlack(listTag), tuple.getBlack());
        assertEquals(String.class, tuple.getRed().get(0).getClass());
    }

    @Test
    public void createInstancesWithCorrectMultipleGenericParameter() {
        TypeTag tag = new TypeTag(ImmutableMap.class, new TypeTag(String.class), new TypeTag(Point.class));
        TypeTag mapTag = new TypeTag(Map.class, new TypeTag(String.class), new TypeTag(Point.class));

        PrefabValueFactory<ImmutableMap> factory = new GuavaCollectionFactory<>("ImmutableMap", MAP);
        Tuple<ImmutableMap> tuple = factory.createValues(tag, prefabValues, null);

        assertEquals(prefabValues.giveRed(mapTag), tuple.getRed());
        assertEquals(prefabValues.giveBlack(mapTag), tuple.getBlack());

        Map.Entry next = (Map.Entry)tuple.getRed().entrySet().iterator().next();
        assertEquals(String.class, next.getKey().getClass());
        assertEquals(Point.class, next.getValue().getClass());
    }
}
