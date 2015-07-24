/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.testhelpers.types.ColorPoint3D;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.StaticFinalContainer;

import org.junit.Test;

public class ObjectAccessorCopyingTest {
    @Test
    public void copyHappyPath() {
        Point original = new Point(2, 3);
        Point copy = copyOf(original);

        assertAllFieldsEqual(original, copy, Point.class);
    }

    @Test
    public void shallowCopy() {
        PointContainer original = new PointContainer(new Point(1, 2));
        PointContainer copy = copyOf(original);

        assertNotSame(original, copy);
        assertTrue(original.getPoint() == copy.getPoint());
    }

    @Test
    public void copyStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        copyOf(foo);
    }

    @Test
    public void inheritanceCopy() {
        Point3D original = new Point3D(2, 3, 4);
        Point3D copy = copyOf(original);

        assertAllFieldsEqual(original, copy, Point.class);
        assertAllFieldsEqual(original, copy, Point3D.class);
    }

    @Test
    public void copyFromSub() {
        Point3D original = new Point3D(2, 3, 4);
        Point copy = copyOf(original, Point.class);

        assertEquals(Point.class, copy.getClass());
        assertAllFieldsEqual(original, copy, Point.class);
    }

    @Test
    public void copyToSub() {
        Point original = new Point(2, 3);
        Point3D copy = copyIntoSubclass(original, Point3D.class);

        assertAllFieldsEqual(original, copy, Point.class);
    }

    @Test
    public void shallowCopyToSub() {
        PointContainer original = new PointContainer(new Point(1, 2));
        SubPointContainer copy = copyIntoSubclass(original, SubPointContainer.class);

        assertNotSame(original, copy);
        assertTrue(original.getPoint() == copy.getPoint());
    }

    @Test
    public void inheritanceCopyToSub() {
        Point3D original = new Point3D(2, 3, 4);
        Point3D copy = copyIntoSubclass(original, ColorPoint3D.class);

        assertAllFieldsEqual(original, copy, Point.class);
        assertAllFieldsEqual(original, copy, Point3D.class);
    }

    @Test
    public void copyToAnonymousSub() {
        Point original = new Point(2, 3);
        ObjectAccessor<Point> accessor = ObjectAccessor.of(original);
        Point copy = accessor.copyIntoAnonymousSubclass();

        assertAllFieldsEqual(original, copy, Point.class);

        assertNotSame(original.getClass(), copy.getClass());
        assertTrue(original.getClass().isAssignableFrom(copy.getClass()));
    }

    private <T> T copyOf(T from) {
        return ObjectAccessor.of(from).copy();
    }

    private <T> T copyOf(T from, Class<T> type) {
        return ObjectAccessor.of(from, type).copy();
    }

    private <T, S extends T> S copyIntoSubclass(T from, Class<S> subclass) {
        return ObjectAccessor.of(from).copyIntoSubclass(subclass);
    }

    private static <T> void assertAllFieldsEqual(T original, T copy, Class<? extends T> type) {
        assertNotSame(original, copy);
        for (Field field : FieldIterable.of(type)) {
            try {
                assertEquals(field.get(original), field.get(copy));
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    static class SubPointContainer extends PointContainer {
        public SubPointContainer(Point point) {
            super(point);
        }
    }
}
