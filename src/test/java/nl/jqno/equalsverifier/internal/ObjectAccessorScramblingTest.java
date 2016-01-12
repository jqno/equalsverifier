/*
 * Copyright 2010, 2012 Jan Ouwens
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
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.StaticFinalContainer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectAccessorScramblingTest {
    private PrefabValues prefabValues;

    @Before
    public void setup() {
        prefabValues = new PrefabValues(new StaticFieldValueStash());
        JavaApiPrefabValues.addTo(prefabValues);
    }

    @Test
    public void scramble() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        assertTrue(original.equals(copy));
        doScramble(copy);
        assertFalse(original.equals(copy));
    }

    @Test
    public void deepScramble() {
        Point3D modified = new Point3D(2, 3, 4);
        Point3D reference = copy(modified);

        doScramble(modified);

        assertFalse(modified.equals(reference));
        modified.z = 4;
        assertFalse(modified.equals(reference));
    }

    @Test
    public void shallowScramble() {
        Point3D modified = new Point3D(2, 3, 4);
        Point3D reference = copy(modified);

        ObjectAccessor.of(modified).shallowScramble(prefabValues, TypeTag.NULL);

        assertFalse(modified.equals(reference));
        modified.z = 4;
        assertTrue(modified.equals(reference));
    }

    @SuppressWarnings("static-access")
    @Test
    public void scrambleStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        int originalInt = StaticFinalContainer.CONST;
        Object originalObject = StaticFinalContainer.OBJECT;

        doScramble(foo);

        assertEquals(originalInt, foo.CONST);
        assertEquals(originalObject, foo.OBJECT);
    }

    @Test
    public void scrambleString() {
        StringContainer foo = new StringContainer();
        String before = foo.s;
        doScramble(foo);
        assertFalse(before.equals(foo.s));
    }

    @Test
    public void privateFinalStringCannotBeScrambled() {
        FinalAssignedStringContainer foo = new FinalAssignedStringContainer();
        String before = foo.s;

        doScramble(foo);

        assertEquals(before, foo.s);
    }

    @Test
    public void scramblePrivateFinalPoint() {
        prefabValues.addFactory(Point.class, new Point(1, 2), new Point(2, 3));
        FinalAssignedPointContainer foo = new FinalAssignedPointContainer();
        Point before = foo.p;

        assertTrue(before.equals(foo.p));
        doScramble(foo);
        assertFalse(before.equals(foo.p));
    }

    private <T> T copy(T object) {
        return ObjectAccessor.of(object).copy();
    }

    private void doScramble(Object object) {
        ObjectAccessor.of(object).scramble(prefabValues, TypeTag.NULL);
    }

    static final class StringContainer {
        private String s = "x";
    }

    static final class FinalAssignedStringContainer {
        private final String s = "x";
    }

    static final class FinalAssignedPointContainer {
        private final Point p = new Point(2, 3);
    }
}
