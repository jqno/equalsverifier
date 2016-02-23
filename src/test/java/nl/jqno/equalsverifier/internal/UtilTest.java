/*
 * Copyright 2016 Jan Ouwens
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

import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class UtilTest {
    @Test
    public void forNameReturnsClass_whenTypeExists() {
        Class<?> actual = Util.classForName("java.util.GregorianCalendar");
        assertEquals(actual, GregorianCalendar.class);
    }

    @Test
    public void forNameReturnsNull_whenTypeDoesntExist() {
        Class<?> actual = Util.classForName("this.type.does.not.exist");
        assertNull(actual);
    }

    @Test
    public void classesReturnsItsArguments() {
        Class<?>[] expected = new Class<?>[] { String.class, Object.class };
        Class<?>[] actual = Util.classes(String.class, Object.class);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void objectsReturnsItsArguments() {
        Object[] expected = new Object[] { "x", new Point(1, 2) };
        Object[] actual = Util.objects("x", new Point(1, 2));
        assertArrayEquals(expected, actual);
    }
}
