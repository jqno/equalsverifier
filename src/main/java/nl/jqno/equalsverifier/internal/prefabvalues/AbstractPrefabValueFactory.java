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

import java.util.List;

public abstract class AbstractPrefabValueFactory<T> implements PrefabValueFactory<T> {
    protected TypeTag determineActualTypeTagFor(int n, TypeTag typeTag) {
        TypeTag objectTypeTag = new TypeTag(Object.class);

        List<TypeTag> genericTypes = typeTag.getGenericTypes();
        if (genericTypes.size() <= n) {
            return objectTypeTag;
        }

        TypeTag innerTag = genericTypes.get(n);
        if (innerTag.getType().equals(TypeTag.Wildcard.class)) {
            return objectTypeTag;
        }

        return innerTag;
    }
}
