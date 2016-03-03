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

import java.util.EnumSet;
import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.Util.classes;
import static nl.jqno.equalsverifier.internal.Util.objects;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates EnumSets
 * using reflection, while taking generics into account.
 */
@SuppressWarnings("rawtypes")
public class ReflectiveEnumSetFactory extends AbstractReflectiveGenericFactory<EnumSet> {
    @Override
    public Tuple<EnumSet> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(EnumSet.class.getName());

        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone, Enum.class);

        EnumSet red = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveRed(entryTag)));
        EnumSet black = (EnumSet)ci.callFactory("of", classes(Enum.class), objects(prefabValues.giveBlack(entryTag)));

        return new Tuple<>(red, black);
    }
}
