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

import java.util.HashMap;
import java.util.Map;

class FactoryCache {
    private final Map<Class<?>, PrefabValueFactory<?>> cache = new HashMap<>();

    public <T> void put(Class<T> type, PrefabValueFactory<T> factory) {
        cache.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T> PrefabValueFactory<T> get(Class<T> type) {
        return (PrefabValueFactory<T>)cache.get(type);
    }

    public boolean contains(Class<?> type) {
        return cache.containsKey(type);
    }
}
