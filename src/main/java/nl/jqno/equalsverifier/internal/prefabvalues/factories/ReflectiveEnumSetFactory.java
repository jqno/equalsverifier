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

import java.util.EnumSet;
import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

public class ReflectiveEnumSetFactory extends AbstractReflectiveGenericFactory<EnumSet> {
    @Override
    public Tuple<EnumSet> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>)typeStack.clone();
        clone.add(tag);

        ConditionalInstantiator ci = new ConditionalInstantiator(EnumSet.class.getName());

        TypeTag entryTag = determineActualTypeTagFor(0, tag);
        if (entryTag.getType().equals(Object.class)) {
            entryTag = new TypeTag(Enum.class);
        }
        prefabValues.realizeCacheFor(entryTag, typeStack);

        EnumSet red = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveRed(entryTag)));
        EnumSet black = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveBlack(entryTag)));

        @SuppressWarnings("unchecked")
        Tuple<EnumSet> result = new Tuple<>(red, black);

        return result;
    }
}
