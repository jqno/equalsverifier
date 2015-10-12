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
package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.TwoElementEnum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FallbackFactoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private FallbackFactory<?> factory;
    private PrefabValues prefabValues;

    @Before
    public void setUp() {
        factory = new FallbackFactory<>();
        prefabValues = new PrefabValues();
        prefabValues.addFactory(int.class, new SimpleFactory<>(42, 1337));
    }

    @Test
    public void dontGiveEmptyEnum() {
        thrown.expect(ReflectionException.class);
        new FallbackFactory<>().createValues(new TypeTag(EmptyEnum.class), new PrefabValues());
    }

    @Test
    public void giveOneElementEnum() {
        assertCorrectTuple(OneElementEnum.class, OneElementEnum.ONE, OneElementEnum.ONE);
    }

    @Test
    public void giveMultiElementEnum() {
        assertCorrectTuple(TwoElementEnum.class, TwoElementEnum.ONE, TwoElementEnum.TWO);
    }

    @Test
    public void giveArray() {
        Tuple<?> tuple = factory.createValues(new TypeTag(int[].class), prefabValues);
        assertArrayEquals(new int[] { 42 }, (int[])tuple.getRed());
        assertArrayEquals(new int[] { 1337 }, (int[])tuple.getBlack());
    }

    @Test
    public void giveClassWithFields() {
        assertCorrectTuple(IntContainer.class, new IntContainer(42, 42), new IntContainer(1337, 1337));
        // assertEquals(-100, IntContainer.staticI);
        assertEquals(-10, IntContainer.STATIC_FINAL_I);
    }

    private <T> void assertCorrectTuple(Class<T> type, T expectedRed, T expectedBlack) {
        Tuple<?> tuple = factory.createValues(new TypeTag(type), prefabValues);
        assertEquals(expectedRed, tuple.getRed());
        assertEquals(expectedBlack, tuple.getBlack());
    }

    private static final class IntContainer {
        private static int staticI = -100;
        private static final int STATIC_FINAL_I = -10;
        @SuppressWarnings("unused") private final int finalI;
        @SuppressWarnings("unused") private int i;

        public IntContainer(int finalI, int i) { this.finalI = finalI; this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
