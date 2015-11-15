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
package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.internal.ConditionalInstantiator;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

public final class SingleParamTagCopyingInstantiatingReflectiveFactory<T> extends AbstractPrefabValueFactory<T> {
    private final String typeName;
    private final Class<?> singleParameterRawType;

    public SingleParamTagCopyingInstantiatingReflectiveFactory(String typeName, Class<?> singleParameterRawType) {
        this.typeName = typeName;
        this.singleParameterRawType = singleParameterRawType;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        TypeTag singleParameterTag = copyGenericTypesInto(singleParameterRawType, tag);
        Object red = ci.instantiate(classes(singleParameterRawType), objects(prefabValues.giveRed(singleParameterTag)));
        Object black = ci.instantiate(classes(singleParameterRawType), objects(prefabValues.giveBlack(singleParameterTag)));

        @SuppressWarnings("unchecked")
        Tuple<T> result = new Tuple<>((T)red, (T)black);

        return result;
    }
}
