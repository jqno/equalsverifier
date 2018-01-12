/*
 * Copyright 2015-2016, 2018 Jan Ouwens
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

import java.util.Comparator;
import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.*;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates maps
 * using reflection, while taking generics into account.
 */
public abstract class ReflectiveMapFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    /* default */ ReflectiveMapFactory(String typeName) {
        this.typeName = typeName;
    }

    public static <T> ReflectiveMapFactory<T> callFactoryMethod(final String typeName, final String methodName) {
        return new ReflectiveMapFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(), objects());
            }
        };
    }

    public static <T> ReflectiveMapFactory<T> callFactoryMethodWithComparator(
            final String typeName, final String methodName, final Object parameterValue) {
        return new ReflectiveMapFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(Comparator.class, Comparator.class), objects(parameterValue, parameterValue));
            }
        };
    }

    protected String getTypeName() {
        return typeName;
    }

    protected abstract Object createEmpty();

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag keyTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);
        TypeTag valueTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);

        Object red = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        Object black = createWith(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));
        Object redCopy = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));

        return Tuple.of(red, black, redCopy);
    }

    private Object createWith(Object key, Object value) {
        Class<?> type = classForName(typeName);
        Object result = createEmpty();
        invoke(type, result, "put", classes(Object.class, Object.class), objects(key, value));
        return result;
    }
}
