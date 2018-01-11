/*
 * Copyright 2018 Jan Ouwens
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

@SuppressWarnings("rawtypes")
public class ThreadLocalFactory extends AbstractReflectiveGenericFactory<ThreadLocal> {
    @Override
    public Tuple<ThreadLocal> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);

        Object redInitial = prefabValues.giveRed(entryTag);
        Object blackInitial = prefabValues.giveBlack(entryTag);

        ThreadLocal red = create(redInitial);
        ThreadLocal black = create(blackInitial);
        return Tuple.of(red, black, red);
    }

    private static ThreadLocal create(final Object value) {
        return new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return value;
            }
        };
    }
}
