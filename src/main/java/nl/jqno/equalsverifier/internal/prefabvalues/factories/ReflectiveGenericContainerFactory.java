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
 * Implementation of {@link PrefabValueFactory} that instantiates objects using
 * reflection, while taking generics into account.
 */
public class ReflectiveGenericContainerFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    public ReflectiveGenericContainerFactory(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        TypeTag internalTag = determineActualTypeTagFor(0, tag);
        Object red = new ConditionalInstantiator(typeName)
                .callFactory("of", classes(Object.class), objects(prefabValues.giveRed(internalTag)));
        Object black = new ConditionalInstantiator(typeName)
                .callFactory("of", classes(Object.class), objects(prefabValues.giveBlack(internalTag)));

        return Tuple.of(red, black);
    }
}
