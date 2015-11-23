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

import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.SimpleFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryCacheTest {
    private static final Class<String> STRING_CLASS = String.class;
    private static final PrefabValueFactory<String> STRING_FACTORY = new SimpleFactory<>("red", "black");
    private static final Class<Integer> INT_CLASS = int.class;
    private static final PrefabValueFactory<Integer> INT_FACTORY = new SimpleFactory<>(42, 1337);

    private FactoryCache cache = new FactoryCache();

    @Test
    public void putAndGetTuple() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertEquals(STRING_FACTORY, cache.get(STRING_CLASS));
    }

    @Test
    public void putTwiceAndGetBoth() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        cache.put(INT_CLASS, INT_FACTORY);

        assertEquals(INT_FACTORY, cache.get(INT_CLASS));
        assertEquals(STRING_FACTORY, cache.get(STRING_CLASS));
    }

    @Test
    public void putNullAndGetNothingBack() {
        cache.put(null, STRING_FACTORY);
        assertNull(cache.get(null));
    }

    @Test
    public void contains() {
        cache.put(STRING_CLASS, STRING_FACTORY);
        assertTrue(cache.contains(STRING_CLASS));
    }

    @Test
    public void doesntContain() {
        assertFalse(cache.contains(STRING_CLASS));
    }
}
