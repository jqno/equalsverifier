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

import java.util.HashMap;
import java.util.Map;

/**
 * Contains a cache of prefabricated values, for {@link PrefabValues}.
 */
class Cache {
    @SuppressWarnings("rawtypes")
    private final Map<TypeTag, Tuple> cache = new HashMap<>();

    /**
     * Adds a prefabricated value to the cache for the given type.
     *
     * @param tag A description of the type. Takes generics into account.
     * @param red A "red" value for the given type.
     * @param black A "black" value for the given type.
     */
    public <T> void put(TypeTag tag, T red, T black) {
        cache.put(tag, new Tuple<>(red, black));
    }

    /**
     * Returns a {@link Tuple} of prefabricated values for the specified type.
     *
     * What happens when there is no value, is undefined. Always call
     * {@link #contains(TypeTag)} first.
     *
     * @param tag A description of the type. Takes generics into account.
     */
    @SuppressWarnings("unchecked")
    public <T> Tuple<T> getTuple(TypeTag tag) {
        return cache.get(tag);
    }

    /**
     * Returns whether prefabricated values are available for the given type.
     *
     * @param tag A description of the type. Takes generics into account.
     */
    public boolean contains(TypeTag tag) {
        return cache.containsKey(tag);
    }
}
