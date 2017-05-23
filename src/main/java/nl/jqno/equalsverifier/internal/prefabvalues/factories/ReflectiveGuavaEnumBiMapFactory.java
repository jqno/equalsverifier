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
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that creates instances of
 * Guava's EnumBiMap using reflection (since Guava may not be on the classpath)
 * while taking generics into account.
 */
public class ReflectiveGuavaEnumBiMapFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private static final String TYPE_NAME = "com.google.common.collect.EnumBiMap";

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone, Enum.class);

        Object red = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        Object black = createWith(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));

        return Tuple.of(red, black);
    }

    @SuppressWarnings("rawtypes")
    private Object createWith(Object key, Object value) {
        Map map = new HashMap();
        invoke(Map.class, map, "put", classes(Object.class, Object.class), objects(key, value));

        ConditionalInstantiator ci = new ConditionalInstantiator(TYPE_NAME);
        return ci.callFactory("create", classes(Map.class), objects(map));
    }
}
