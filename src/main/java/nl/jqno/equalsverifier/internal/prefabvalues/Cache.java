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

class Cache {
    final Map<TypeTag, Tuple> cache = new HashMap<>();

    public <T> void put(TypeTag tag, T red, T black) {
        cache.put(tag, new Tuple<>(red, black));
    }

    @SuppressWarnings("unchecked")
    public <T> Tuple<T> getTuple(TypeTag tag) {
        return cache.get(tag);
    }

    public boolean contains(TypeTag tag) {
        return cache.containsKey(tag);
    }
}
