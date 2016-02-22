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
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates collections
 * using reflection, while taking generics into account.
 */
public abstract class ReflectiveCollectionFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    /* default */ ReflectiveCollectionFactory(String typeName) {
        this.typeName = typeName;
    }

    public static <T> ReflectiveCollectionFactory<T> callFactoryMethod(final String typeName, final String methodName) {
        return new ReflectiveCollectionFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(), objects());
            }
        };
    }

    public static <T> ReflectiveCollectionFactory<T> callFactoryMethodWithParameter(
            final String typeName, final String methodName, final Class<?> parameterType, final Object parameterValue) {
        return new ReflectiveCollectionFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(parameterType), objects(parameterValue));
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
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);

        Object red = createWith(prefabValues.giveRed(entryTag));
        Object black = createWith(prefabValues.giveBlack(entryTag));

        return Tuple.of(red, black);
    }

    private Object createWith(Object value) {
        Object result = createEmpty();
        invoke(ConditionalInstantiator.forName(typeName), result, "add", classes(Object.class), objects(value));
        return result;
    }
}
