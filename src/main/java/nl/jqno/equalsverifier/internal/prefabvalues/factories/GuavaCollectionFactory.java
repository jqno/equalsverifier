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

public final class GuavaCollectionFactory<T> extends AbstractReflectiveGenericFactory<T> {
    private static final String PACKAGE_NAME = "com.google.common.collect.";
    private static final String FACTORY_METHOD = "of";

    private final String typeName;
    private final Kind kind;

    public enum Kind {
        COLLECTION, MAP
    }

    public GuavaCollectionFactory(String typeName, Kind kind) {
        this.typeName = PACKAGE_NAME + typeName;
        this.kind = kind;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        Tuple<Object[]> values = values(prefabValues, tag);

        Object red = ci.callFactory(typeName, FACTORY_METHOD, types(), values.getRed());
        Object black = ci.callFactory(typeName, FACTORY_METHOD, types(), values.getBlack());

        @SuppressWarnings("unchecked")
        Tuple<T> result = new Tuple<>((T)red, (T)black);

        return result;
    }

    private Class<?>[] types() {
        switch (kind) {
            case COLLECTION:
                return classes(Object.class);
            case MAP:
                return classes(Object.class, Object.class);
            default:
                return classes();
        }
    }

    private Tuple<Object[]> values(PrefabValues prefabValues, TypeTag tag) {
        switch (kind) {
            case COLLECTION:
                TypeTag genericType = determineActualTypeTagFor(0, tag);
                return new Tuple<>(
                        new Object[] { prefabValues.giveRed(genericType) },
                        new Object[] { prefabValues.giveBlack(genericType) });
            case MAP:
                TypeTag keyType = determineActualTypeTagFor(0, tag);
                TypeTag valueType = determineActualTypeTagFor(1, tag);
                return new Tuple<>(
                        new Object[] { prefabValues.giveRed(keyType), prefabValues.giveBlack(valueType) },
                        new Object[] { prefabValues.giveBlack(keyType), prefabValues.giveBlack(valueType) });
            default:
                return null;
        }
    }
}
