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
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierBugException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TypeTag {
    private final Class<?> type;
    private final List<TypeTag> genericTypes;

    public TypeTag(Class<?> type, TypeTag... genericTypes) {
        this(type, Arrays.asList(genericTypes));
    }

    private TypeTag(Class<?> type, List<TypeTag> genericTypes) {
        this.type = type;
        this.genericTypes = genericTypes;
    }

    public static TypeTag of(Field field) {
        return resolve(field.getGenericType());
    }

    private static TypeTag resolve(Type type) {
        List<TypeTag> nestedTags = new ArrayList<>();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            Type[] typeArgs = pt.getActualTypeArguments();
            for (Type typeArg : typeArgs) {
                nestedTags.add(resolve(typeArg));
            }
            return new TypeTag((Class)pt.getRawType(), nestedTags);
        }
        else if (type instanceof Class) {
            return new TypeTag((Class)type, nestedTags);
        }
        throw new EqualsVerifierBugException("Failed to tag type " + type.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TypeTag)) {
            return false;
        }
        TypeTag other = (TypeTag)obj;
        return type.equals(other.type) && genericTypes.equals(other.genericTypes);
    }

    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + type.hashCode();
        result = (59 * result) + genericTypes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("TypeTag: ");
        s.append(type.getSimpleName());
        if (genericTypes.size() >= 1) {
            s.append("<");
            s.append(genericTypes.get(0));
        }
        for (int i = 1; i < genericTypes.size(); i++) {
            s.append(", ");
            s.append(genericTypes.get(i));
        }
        if (genericTypes.size() >= 1) {
            s.append(">");
        }
        return s.toString();
    }
}
