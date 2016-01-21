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

import com.google.common.collect.EnumBiMap;
import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ReflectiveGuavaEnumBiMapFactoryTest {
    private final ReflectiveGuavaEnumBiMapFactory factory = new ReflectiveGuavaEnumBiMapFactory();
    private final PrefabValues prefabValues = new PrefabValues(new StaticFieldValueStash());
    private final LinkedHashSet<TypeTag> emptyTypeStack = new LinkedHashSet<>();
    private EnumBiMap<TwoElementEnum, TwoElementEnum> expectedRed;
    private EnumBiMap<TwoElementEnum, TwoElementEnum> expectedBlack;

    @Before
    public void setUp() {
        prefabValues.addFactory(Enum.class, TwoElementEnum.ONE, TwoElementEnum.TWO);

        expectedRed = EnumBiMap.create(TwoElementEnum.class, TwoElementEnum.class);
        expectedRed.put(TwoElementEnum.ONE, TwoElementEnum.TWO);
        expectedBlack = EnumBiMap.create(TwoElementEnum.class, TwoElementEnum.class);
        expectedBlack.put(TwoElementEnum.TWO, TwoElementEnum.TWO);
    }

    @Test
    public void createSpecificEnumSet() {
        TypeTag tag = new TypeTag(EnumBiMap.class, new TypeTag(TwoElementEnum.class), new TypeTag(TwoElementEnum.class));
        Tuple<?> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }

    @Test
    public void createWildcardEnumSet() {
        TypeTag tag = new TypeTag(EnumBiMap.class, new TypeTag(TypeTag.Wildcard.class), new TypeTag(TypeTag.Wildcard.class));
        Tuple<?> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }

    @Test
    public void createRawEnumSet() {
        TypeTag tag = new TypeTag(EnumBiMap.class);
        Tuple<?> tuple = factory.createValues(tag, prefabValues, emptyTypeStack);

        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }
}
