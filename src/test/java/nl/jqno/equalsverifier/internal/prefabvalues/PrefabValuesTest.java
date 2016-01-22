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
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;

public class PrefabValuesTest {
    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final TypeTag POINT_TAG = new TypeTag(Point.class);
    private static final TypeTag INT_TAG = new TypeTag(int.class);
    private static final TypeTag STRING_ARRAY_TAG = new TypeTag(String[].class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PrefabValues pv = new PrefabValues();

    @Before
    public void setUp() {
        pv.addFactory(String.class, new AppendingStringTestFactory());
        pv.addFactory(int.class, 42, 1337);
    }

    @Test
    public void sanityTestFactoryIncreasesStringLength() {
        AppendingStringTestFactory f = new AppendingStringTestFactory();
        assertEquals("r", f.createValues(null, null, null).getRed());
        assertEquals("rr", f.createValues(null, null, null).getRed());
        assertEquals("rrr", f.createValues(null, null, null).getRed());
    }

    @Test
    public void giveRedFromFactory() {
        assertEquals("r", pv.giveRed(STRING_TAG));
    }

    @Test
    public void giveRedFromCache() {
        pv.giveRed(STRING_TAG);
        assertEquals("r", pv.giveRed(STRING_TAG));
    }

    @Test
    public void giveBlackFromFactory() {
        assertEquals("b", pv.giveBlack(STRING_TAG));
    }

    @Test
    public void giveBlackFromCache() {
        pv.giveBlack(STRING_TAG);
        assertEquals("b", pv.giveBlack(STRING_TAG));
    }

    @Test
    public void giveRedFromFallbackFactory() {
        Point actual = pv.giveRed(POINT_TAG);
        assertEquals(new Point(42, 42), actual);
    }

    @Test
    public void giveBlackFromFallbackFactory() {
        Point actual = pv.giveBlack(POINT_TAG);
        assertEquals(new Point(1337, 1337), actual);
    }

    @Test
    public void giveOtherWhenValueIsKnown() {
        Point red = pv.giveRed(POINT_TAG);
        Point black = pv.giveBlack(POINT_TAG);
        assertEquals(black, pv.giveOther(POINT_TAG, red));
        assertEquals(red, pv.giveOther(POINT_TAG, black));
    }

    @Test
    public void giveOtherWhenValueIsCloneOfKnown() {
        Point red = new Point(42, 42);
        Point black = new Point(1337, 1337);
        assertEquals(black, pv.giveOther(POINT_TAG, red));
        assertEquals(red, pv.giveOther(POINT_TAG, black));

        // Sanity check
        assertEquals(red, pv.giveRed(POINT_TAG));
        assertEquals(black, pv.giveBlack(POINT_TAG));
    }

    @Test
    public void giveOtherWhenValueIsUnknown() {
        Point value = new Point(-1, -1);
        Point expected = pv.giveRed(POINT_TAG);
        assertEquals(expected, pv.giveOther(POINT_TAG, value));
    }

    @Test
    public void giveOtherWhenValueIsPrimitive() {
        int expected = pv.giveRed(INT_TAG);
        assertEquals(expected, (int)pv.giveOther(INT_TAG, -10));
    }

    @Test
    public void giveOtherWhenValueIsNull() {
        Point expected = pv.giveRed(POINT_TAG);
        assertEquals(expected, pv.giveOther(POINT_TAG, null));
    }

    @Test
    public void giveOtherWhenValueIsKnownArray() {
        String[] red = pv.giveRed(STRING_ARRAY_TAG);
        String[] black = pv.giveBlack(STRING_ARRAY_TAG);
        assertArrayEquals(black, pv.giveOther(STRING_ARRAY_TAG, red));
        assertArrayEquals(red, pv.giveOther(STRING_ARRAY_TAG, black));
    }

    @Test
    public void giveOtherWhenValueIsCloneOfKnownArray() {
        String[] red = { "r" };
        String[] black = { "b" };
        assertArrayEquals(black, pv.giveOther(STRING_ARRAY_TAG, red));
        assertArrayEquals(red, pv.giveOther(STRING_ARRAY_TAG, black));

        // Sanity check
        assertArrayEquals(red, pv.<String[]>giveRed(STRING_ARRAY_TAG));
        assertArrayEquals(black, pv.<String[]>giveBlack(STRING_ARRAY_TAG));
    }

    @Test
    public void giveOtherWhenValueIsUnknownArray() {
        String[] value = { "hello world" };
        String[] expected = pv.giveRed(STRING_ARRAY_TAG);
        assertArrayEquals(expected, pv.giveOther(STRING_ARRAY_TAG, value));
    }

    @Test
    public void giveOtherWhenTagDoesntMatchValue() {
        thrown.expect(ReflectionException.class);
        thrown.expectMessage("TypeTag does not match value.");
        pv.giveOther(POINT_TAG, "not a Point");
    }

    @Test
    public void fallbackDoesNotAffectStaticFields() {
        int expected = StaticContainer.staticInt;
        pv.giveRed(new TypeTag(StaticContainer.class));
        assertEquals(expected, StaticContainer.staticInt);
    }

    @Test
    public void stringListIsSeparateFromIntegerList() {
        pv.addFactory(List.class, new ListTestFactory());

        List<String> strings = pv.giveRed(new TypeTag(List.class, STRING_TAG));
        List<Integer> ints = pv.giveRed(new TypeTag(List.class, INT_TAG));

        assertEquals("r", strings.get(0));
        assertEquals(42, (int)ints.get(0));
    }

    @Test
    public void addingNullDoesntBreakAnything() {
        pv.addFactory(null, new ListTestFactory());
    }

    @Test
    public void addingATypeTwiceOverrulesTheExistingOne() {
        pv.addFactory(int.class, -1, -2);
        assertEquals(-1, pv.giveRed(INT_TAG));
        assertEquals(-2, pv.giveBlack(INT_TAG));
    }

    private static class AppendingStringTestFactory implements PrefabValueFactory<String> {
        private String red;
        private String black;

        public AppendingStringTestFactory() { red = ""; black = ""; }

        @Override
        public Tuple<String> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            red += "r"; black += "b";
            return new Tuple<>(red, black);
        }
    }

    @SuppressWarnings("rawtypes")
    private static class ListTestFactory implements PrefabValueFactory<List> {
        @Override
        @SuppressWarnings("unchecked")
        public Tuple<List> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            TypeTag subtag = tag.getGenericTypes().get(0);

            List red = new ArrayList<>();
            red.add(prefabValues.giveRed(subtag));

            List black = new ArrayList<>();
            black.add(prefabValues.giveBlack(subtag));

            return new Tuple<>(red, black);
        }
    }

    private static class StaticContainer {
        static int staticInt = 2;
        @SuppressWarnings("unused")
        int regularInt = 3;
    }
}
