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
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class SuperclassIterableTest {
    private List<Class<?>> actual;

    @Before
    public void setUp() {
        actual = new ArrayList<>();
    }

    @Test
    public void simpleClassExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleClass.class)) {
            actual.add(type);
        }
        assertEquals(emptyList(), actual);
    }

    @Test
    public void simpleClassIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleClass.class)) {
            actual.add(type);
        }
        assertEquals(singletonList(SimpleClass.class), actual);
    }

    @Test
    public void hierarchyExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleSubclass.class, SimpleClass.class), actual);
    }

    @Test
    public void hierarchyIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleSubSubclass.class)) {
            actual.add(type);
        }
        assertEquals(asList(SimpleSubSubclass.class, SimpleSubclass.class, SimpleClass.class), actual);
    }

    @Test
    public void interfaceExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleInterface.class)) {
            actual.add(type);
        }
        assertEquals(emptyList(), actual);
    }

    @Test
    public void interfaceIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleInterface.class)) {
            actual.add(type);
        }
        assertEquals(singletonList(SimpleInterface.class), actual);
    }

    @Test
    public void subInterfaceExcludeSelf() {
        for (Class<?> type : SuperclassIterable.of(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertEquals(emptyList(), actual);
    }

    @Test
    public void subInterfaceIncludeSelf() {
        for (Class<?> type : SuperclassIterable.ofIncludeSelf(SimpleSubInterface.class)) {
            actual.add(type);
        }
        assertEquals(singletonList(SimpleSubInterface.class), actual);
    }

    static class SimpleClass {}
    static class SimpleSubclass extends SimpleClass {}
    static class SimpleSubSubclass extends SimpleSubclass {}

    interface SimpleInterface {}
    static class SimpleSubInterface implements SimpleInterface {}
}
