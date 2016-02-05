/*
 * Copyright 2015-2016 Jan Ouwens
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
package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;

/**
 * Creates instances of generic types for use as prefab value.
 *
 * @param <T> The type to instantiate.
 */
public interface PrefabValueFactory<T> {
    /**
     * Creates a tuple of two prefab values.
     *
     * @param tag The typetag of the type for which to create values.
     * @param prefabValues Repository for querying instances of generic types
     *          of the type tag.
     * @param typeStack A stack of {@link TypeTag}s that require tag in order
     *          to be created. Used for recursion detection.
     * @return A "red" instance of {@link T}.
     */
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack);
}
