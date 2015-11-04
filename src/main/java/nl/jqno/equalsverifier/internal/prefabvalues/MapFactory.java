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

import java.util.LinkedHashSet;
import java.util.Map;

public abstract class MapFactory<T extends Map> extends AbstractCollectionFactory<T> {
    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>)typeStack.clone();
        clone.add(tag);

        TypeTag keyTag = determineActualTypeTagFor(0, tag);
        TypeTag valueTag = determineActualTypeTagFor(1, tag);
        prefabValues.realizeCacheFor(keyTag, typeStack);
        prefabValues.realizeCacheFor(valueTag, typeStack);

        // Use red for key and black for value in the Red map to avoid having identical keys and values.
        // But don't do it in the Black map, or they may cancel each other out again.
        T red = createEmpty();
        red.put(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        T black = createEmpty();
        black.put(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));

        return new Tuple<>(red, black);
    }
}
