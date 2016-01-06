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
package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Implementation of {@link PrefabValueFactory} that specializes in creating
 * implementations of {@link Map}, taking generics into account.
 */
public abstract class MapFactory<T extends Map> extends AbstractReflectiveGenericFactory<T> {
    public abstract T createEmpty();

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);

        TypeTag keyTag = determineActualTypeTagFor(0, tag);
        TypeTag valueTag = determineActualTypeTagFor(1, tag);
        prefabValues.realizeCacheFor(keyTag, clone);
        prefabValues.realizeCacheFor(valueTag, clone);

        // Use red for key and black for value in the Red map to avoid having identical keys and values.
        // But don't do it in the Black map, or they may cancel each other out again.
        T red = createEmpty();
        red.put(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        T black = createEmpty();
        black.put(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));

        return new Tuple<>(red, black);
    }
}
