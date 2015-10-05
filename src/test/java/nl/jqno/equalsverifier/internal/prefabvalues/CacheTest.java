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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CacheTest {
    private static final TypeTag STRING_TAG = new TypeTag(String.class);
    private static final Tuple<String> STRING_TUPLE = new Tuple<>("red", "black");
    private static final TypeTag INT_TAG = new TypeTag(int.class);
    private static final Tuple<Integer> INT_TUPLE = new Tuple<>(42, 1337);

    private Cache cache = new Cache();

    @Test
    public void putAndGetTuple() {
        cache.put(STRING_TAG, STRING_TUPLE.getRed(), STRING_TUPLE.getBlack());
        assertEquals(STRING_TUPLE, cache.getTuple(STRING_TAG));
    }

    @Test
    public void putTwiceAndGetBoth() {
        cache.put(STRING_TAG, STRING_TUPLE.getRed(), STRING_TUPLE.getBlack());
        cache.put(INT_TAG, INT_TUPLE.getRed(), INT_TUPLE.getBlack());

        assertEquals(INT_TUPLE, cache.getTuple(INT_TAG));
        assertEquals(STRING_TUPLE, cache.getTuple(STRING_TAG));
    }

    @Test
    public void contains() {
        cache.put(STRING_TAG, STRING_TUPLE.getRed(), STRING_TUPLE.getBlack());
        assertTrue(cache.contains(STRING_TAG));
    }

    @Test
    public void doesntContain() {
        assertFalse(cache.contains(STRING_TAG));
    }
}
