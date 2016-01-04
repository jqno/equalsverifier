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

import nl.jqno.equalsverifier.internal.ConditionalInstantiator;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

/**
 * Implementation of {@link PrefabValueFactory} that creates instances of
 * Guava's EnumBiMap using reflection (since Guava may not be on the classpath)
 * while taking generics into account.
 */
public class ReflectiveGuavaEnumBiMapFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private static final String TYPE_NAME = "com.google.common.collect.EnumBiMap";

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>)typeStack.clone();
        clone.add(tag);

        TypeTag keyTag = makeSureItsAnEnum(determineActualTypeTagFor(0, tag));
        TypeTag valueTag = makeSureItsAnEnum(determineActualTypeTagFor(1, tag));
        prefabValues.realizeCacheFor(keyTag, typeStack);
        prefabValues.realizeCacheFor(valueTag, typeStack);

        T red = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        T black = createWith(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));

        return new Tuple<>(red, black);
    }

    private TypeTag makeSureItsAnEnum(TypeTag tag) {
        if (tag.getType().equals(Object.class)) {
            return new TypeTag(Enum.class);
        }
        return tag;
    }

    private T createWith(Object key, Object value) {
        @SuppressWarnings("unchecked")
        Map map = new HashMap();
        try {
            Method add = Map.class.getMethod("put", Object.class, Object.class);
            add.invoke(map, key, value);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }

        ConditionalInstantiator ci = new ConditionalInstantiator(TYPE_NAME);
        @SuppressWarnings("unchecked")
        T result = (T)ci.callFactory("create", classes(Map.class), objects(map));
        return result;
    }
}
