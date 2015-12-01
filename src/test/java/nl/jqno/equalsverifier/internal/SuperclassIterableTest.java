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
package nl.jqno.equalsverifier.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class SuperclassIterableTest {
    private List<Class<?>> actual;

    @Before
    public void setUp() {
        actual = new ArrayList<>();
    }

    @Test
    public void simpleClass() {
        for (Class<?> type : SuperclassIterable.of(SimpleClass.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleClass.class), actual);
    }

    @Test
    public void hierarchy() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleSubSubclass.class, SimpleSubclass.class, SimpleClass.class), actual);
    }

    @Test
    public void anInterface() {
        for (Class<?> type : SuperclassIterable.of(SimpleInterface.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleInterface.class), actual);
    }

    @Test
    public void subInterface() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleSubInterface.class), actual);
    }

    static class SimpleClass {}
    static class SimpleSubclass extends SimpleClass {}
    static class SimpleSubSubclass extends SimpleSubclass {}

    interface SimpleInterface {}
    static class SimpleSubInterface implements SimpleInterface {}
}
