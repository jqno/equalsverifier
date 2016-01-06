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
import java.util.Comparator;
import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

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

        TypeTag keyTag = determineActualTypeTagFor(0, tag);
        TypeTag valueTag = determineActualTypeTagFor(1, tag);
        prefabValues.realizeCacheFor(keyTag, clone);
        prefabValues.realizeCacheFor(valueTag, clone);

        T red = createWith(prefabValues.giveRed(keyTag), prefabValues.giveBlack(valueTag));
        T black = createWith(prefabValues.giveBlack(keyTag), prefabValues.giveBlack(valueTag));

        return new Tuple<>(red, black);
    }

    private T createWith(Object key, Object value) {
        Class<?> type = ConditionalInstantiator.forName(typeName);
        T result = (T)createEmpty();
        try {
            Method add = type.getMethod("put", Object.class, Object.class);
            add.invoke(result, key, value);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
        return result;
    }
}
