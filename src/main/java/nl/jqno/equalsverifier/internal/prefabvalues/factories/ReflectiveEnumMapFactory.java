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

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumMaps
 * using reflection, while taking generics into account.
 */
public class ReflectiveEnumMapFactory extends AbstractReflectiveGenericFactory<EnumMap> {
    @Override
    public Tuple<EnumMap> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone, Enum.class);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);

        EnumMap red = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        EnumMap black = createWith(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));

        return new Tuple<>(red, black);
    }

    private EnumMap createWith(Object key, Object value) {
        @SuppressWarnings("unchecked")
        Map result = new HashMap();
        try {
            Method add = Map.class.getMethod("put", Object.class, Object.class);
            add.invoke(result, key, value);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
        return new EnumMap<>(result);
    }
}
