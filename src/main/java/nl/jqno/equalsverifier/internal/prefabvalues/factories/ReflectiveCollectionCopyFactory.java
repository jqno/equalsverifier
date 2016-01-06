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
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

/**
 * Implementation of {@link PrefabValueFactory} that reflectively instantiates
 * collections by copying them from a collection that was already instantiated.
 */
public final class ReflectiveCollectionCopyFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;
    private final Class<?> declaredParameterRawType;
    private final Class<?> actualParameterRawType;
    private final String factoryType;
    private final String factoryMethod;

    public ReflectiveCollectionCopyFactory(String typeName, Class<?> parameterRawType, String factoryType, String factoryMethod) {
        this(typeName, parameterRawType, parameterRawType, factoryType, factoryMethod);
    }

    public ReflectiveCollectionCopyFactory(String typeName, Class<?> declaredParameterRawType, Class<?> actualParameterRawType,
            String factoryType, String factoryMethod) {
        this.typeName = typeName;
        this.declaredParameterRawType = declaredParameterRawType;
        this.actualParameterRawType = actualParameterRawType;
        this.factoryType = factoryType;
        this.factoryMethod = factoryMethod;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);

        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        TypeTag singleParameterTag = copyGenericTypesInto(actualParameterRawType, tag);
        prefabValues.realizeCacheFor(singleParameterTag, clone);

        Object red = ci.callFactory(factoryType, factoryMethod,
                classes(declaredParameterRawType), objects(prefabValues.giveRed(singleParameterTag)));
        Object black = ci.callFactory(factoryType, factoryMethod,
                classes(declaredParameterRawType), objects(prefabValues.giveBlack(singleParameterTag)));

        @SuppressWarnings("unchecked")
        Tuple<T> result = new Tuple<>((T)red, (T)black);

        return result;
    }
}
