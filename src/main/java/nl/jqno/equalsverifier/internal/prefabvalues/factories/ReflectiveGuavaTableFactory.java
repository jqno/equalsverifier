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
 * Implementation of {@link PrefabValueFactory} that creates instances of
 * Guava's Table using reflection (since Guava may not be on the classpath)
 * while taking generics into account.
 */
public abstract class ReflectiveGuavaTableFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    /* default */ ReflectiveGuavaTableFactory(String typeName) {
        this.typeName = typeName;
    }

    public static <T> ReflectiveGuavaTableFactory<T> callFactoryMethod(final String typeName, final String methodName) {
        return new ReflectiveGuavaTableFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(), objects());
            }
        };
    }

    public static <T> ReflectiveGuavaTableFactory<T> callFactoryMethodWithComparator(
            final String typeName, final String methodName, final Object parameterValue) {
        return new ReflectiveGuavaTableFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(Comparator.class, Comparator.class), objects(parameterValue, parameterValue));
            }
        };
    }

    protected abstract Object createEmpty();

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag columnTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);
        TypeTag rowTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);
        TypeTag valueTag = determineAndCacheActualTypeTag(2, tag, prefabValues, clone);

        Object red = createWith(prefabValues.giveRed(columnTag), prefabValues.giveRed(rowTag), prefabValues.giveBlack(valueTag));
        Object black = createWith(prefabValues.giveBlack(columnTag), prefabValues.giveBlack(rowTag), prefabValues.giveBlack(valueTag));

        return Tuple.of(red, black);
    }

    private Object createWith(Object column, Object row, Object value) {
        Class<?> type = ConditionalInstantiator.forName(typeName);
        if (type == null) {
            return null;
        }
        Object result = createEmpty();
        try {
            Method add = type.getMethod("put", Object.class, Object.class, Object.class);
            add.invoke(result, column, row, value);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
        return result;
    }
}
