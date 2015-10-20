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

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierBugException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a generic type, including raw type and generic type parameters.
 *
 * If the type is not generic, the genericTypes list will be empty.
 *
 * @author Jan Ouwens
 */
public final class TypeTag {
    private final Class<?> type;
    private final List<TypeTag> genericTypes;

    /**
     * Constructor.
     *
     * @param type The raw type.
     * @param genericTypes A list of TypeTags for each generic type parameter.
     */
    public TypeTag(Class<?> type, TypeTag... genericTypes) {
        this(type, Arrays.asList(genericTypes));
    }

    private TypeTag(Class<?> type, List<TypeTag> genericTypes) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.type = type;
        this.genericTypes = genericTypes;
    }

    /**
     * Temporary factory
     */
    public static TypeTag make(Class<?> type) {
        return new TypeTag(type);
    }

    /**
     * Resolves a TypeTag from the type of a {@link Field} instance.
     *
     * @param field The field to resolve.
     * @return The TypeTag for the given field.
     */
    public static TypeTag of(Field field) {
        return resolve(field.getGenericType());
    }

    private static TypeTag resolve(Type type) {
        List<TypeTag> nestedTags = new ArrayList<>();
        if (type instanceof Class) {
            return new TypeTag((Class)type, nestedTags);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            Type[] typeArgs = pt.getActualTypeArguments();
            for (Type typeArg : typeArgs) {
                nestedTags.add(resolve(typeArg));
            }
            return new TypeTag((Class)pt.getRawType(), nestedTags);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)type;
            Type t = gat.getGenericComponentType();
            return new TypeTag(GenericArray.class, resolve(t));
        }
        if (type instanceof WildcardType) {
            return new TypeTag(Wildcard.class);
        }
        if (type instanceof java.lang.reflect.TypeVariable) {
            return new TypeTag(TypeTag.TypeVariable.class);
        }
        throw new EqualsVerifierBugException("Failed to tag type " + type.toString() + " (" + type.getClass() + ")");
    }

    /**
     * Getter for the TypeTag's raw type.
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getType() {
        return (Class<T>)type;
    }

    /**
     * Getter for the TypeTag's generic types.
     */
    public List<TypeTag> getGenericTypes() {
        return Collections.unmodifiableList(genericTypes);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TypeTag)) {
            return false;
        }
        TypeTag other = (TypeTag)obj;
        return type.equals(other.type) && genericTypes.equals(other.genericTypes);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + type.hashCode();
        result = (59 * result) + genericTypes.hashCode();
        return result;
    }

    /**
     * @inheritDoc
     */
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

    /**
     * Represents a wildcard type parameter like {@code List<?>}.
     */
    public static final class Wildcard {}

    /**
     * Represents a variable type parameter like {@code List<T>}.
     */
    public static final class TypeVariable {}

    /**
     * Represents a generic array type parameter like {@code List<T>[]}.
     */
    public static final class GenericArray {}
}
