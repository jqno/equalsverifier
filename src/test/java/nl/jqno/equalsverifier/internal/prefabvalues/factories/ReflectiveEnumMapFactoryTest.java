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

import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag.Wildcard;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class ReflectiveEnumMapFactoryTest {
    private static final Object RED_OBJECT = new Object();
    private static final Object BLACK_OBJECT = new Object();
    private final ReflectiveEnumMapFactory factory = new ReflectiveEnumMapFactory();
    private final PrefabValues prefabValues = new PrefabValues(new StaticFieldValueStash());
    private final LinkedHashSet<TypeTag> emptyTypeStack = new LinkedHashSet<>();
    private final EnumMap<TwoElementEnum, Object> expectedRed = new EnumMap<>(TwoElementEnum.class);
    private final EnumMap<TwoElementEnum, Object> expectedBlack = new EnumMap<>(TwoElementEnum.class);

    @Before
    public void setUp() {
        prefabValues.addFactory(Enum.class, TwoElementEnum.ONE, TwoElementEnum.TWO);
        prefabValues.addFactory(Object.class, RED_OBJECT, BLACK_OBJECT);
        expectedRed.put(TwoElementEnum.ONE, BLACK_OBJECT);
        expectedBlack.put(TwoElementEnum.TWO, BLACK_OBJECT);
    }

    @Test
    public void createSpecificEnumSet() {
        TypeTag tag = new TypeTag(EnumMap.class, new TypeTag(TwoElementEnum.class), new TypeTag(Object.class));
        Tuple<EnumMap> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }

    @Test
    public void createWildcardEnumSet() {
        TypeTag tag = new TypeTag(EnumMap.class, new TypeTag(Wildcard.class), new TypeTag(Wildcard.class));
        Tuple<EnumMap> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }

    @Test
    public void createRawEnumSet() {
        TypeTag tag = new TypeTag(EnumMap.class);
        Tuple<EnumMap> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }
}
