/*
 * Copyright 2015-2016 Jan Ouwens
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

import com.google.common.base.Optional;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class ReflectiveGenericContainerFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG = new TypeTag(Map.class, STRING_TYPETAG, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDMAP_TYPETAG = new TypeTag(Map.class, OBJECT_TYPETAG, OBJECT_TYPETAG);
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);

    private static final ReflectiveGenericContainerFactory<Optional> OPTIONAL_FACTORY =
            new ReflectiveGenericContainerFactory<>("com.google.common.base.Optional", "of", Object.class);

    private final PrefabValues prefabValues = new PrefabValues();
    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;

    @Before
    public void setUp() {
        JavaApiPrefabValues.addTo(prefabValues);
        red = prefabValues.giveRed(STRING_TYPETAG);
        black = prefabValues.giveBlack(STRING_TYPETAG);
        redObject = prefabValues.giveRed(OBJECT_TYPETAG);
        blackObject = prefabValues.giveBlack(OBJECT_TYPETAG);
    }

    @Test
    public void createOptionalsOfString() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(STRINGSTRINGMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(Optional.of(red), tuple.getRed());
        assertEquals(Optional.of(black), tuple.getBlack());
    }

    @Test
    public void createOptionalsOfWildcard() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(WILDCARDMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(Optional.of(redObject), tuple.getRed());
        assertEquals(Optional.of(blackObject), tuple.getBlack());
    }

    @Test
    public void createRawOptionals() {
        Tuple<Optional> tuple = OPTIONAL_FACTORY.createValues(RAWMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(Optional.of(redObject), tuple.getRed());
        assertEquals(Optional.of(blackObject), tuple.getBlack());
    }
}
