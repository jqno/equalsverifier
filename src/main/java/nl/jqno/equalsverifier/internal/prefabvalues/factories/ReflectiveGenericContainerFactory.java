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

import static nl.jqno.equalsverifier.internal.Util.classes;
import static nl.jqno.equalsverifier.internal.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates objects using
 * reflection, while taking generics into account.
 */
public class ReflectiveGenericContainerFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;
    private final String factoryMethod;
    private final Class<?> parameterType;

    public ReflectiveGenericContainerFactory(String typeName, String factoryMethod, final Class<?> parameterType) {
        this.typeName = typeName;
        this.factoryMethod = factoryMethod;
        this.parameterType = parameterType;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        TypeTag internalTag = determineActualTypeTagFor(0, tag);

        Object red = createWith(prefabValues.giveRed(internalTag));
        Object black = createWith(prefabValues.giveBlack(internalTag));

        return Tuple.of(red, black);
    }

    @SuppressWarnings("rawtypes")
    private Object createWith(Object value) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        return ci.callFactory(factoryMethod, classes(parameterType), objects(value));
    }
}
