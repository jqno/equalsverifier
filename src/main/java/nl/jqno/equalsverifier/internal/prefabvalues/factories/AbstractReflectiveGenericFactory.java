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

import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReflectiveGenericFactory<T> implements PrefabValueFactory<T> {
    public static final TypeTag OBJECT_TYPE_TAG = new TypeTag(Object.class);

    protected TypeTag copyGenericTypesInto(Class<?> type, TypeTag source) {
        List<TypeTag> genericTypes = new ArrayList<>();
        for (TypeTag tag : source.getGenericTypes()) {
            genericTypes.add(makeConcrete(tag));
        }
        return new TypeTag(type, genericTypes.toArray(new TypeTag[genericTypes.size()]));
    }

    protected TypeTag determineActualTypeTagFor(int n, TypeTag typeTag) {
        List<TypeTag> genericTypes = typeTag.getGenericTypes();
        if (genericTypes.size() <= n) {
            return OBJECT_TYPE_TAG;
        }

        TypeTag innerTag = genericTypes.get(n);
        return makeConcrete(innerTag);
    }

    private TypeTag makeConcrete(TypeTag tag) {
        if (tag.getType().equals(TypeTag.Wildcard.class)) {
            return OBJECT_TYPE_TAG;
        }

        return tag;
    }
}
