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
package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierBugException;

import java.lang.reflect.*;
import java.util.*;

import static nl.jqno.equalsverifier.internal.Util.classForName;

/**
 * Represents a generic type, including raw type and generic type parameters.
 *
 * If the type is not generic, the genericTypes list will be empty.
 *
 * @author Jan Ouwens
 */
public final class TypeTag {
    /**
     *  Null object for TypeTag.
     */
    public static final TypeTag NULL = new TypeTag(NullType.class);

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
     * Resolves a TypeTag from the type of a {@link Field} instance, using an
     * enclosing type to determine any generic parameters the field may contain.
     *
     * @param field The field to resolve.
     * @param enclosingType The type that contains the field, used to determine
     *                      any generic parameters it may contain.
     * @return The TypeTag for the given field.
     */
    public static TypeTag of(Field field, TypeTag enclosingType) {
        return resolve(field.getGenericType(), enclosingType, false);
    }

    private static TypeTag resolve(Type type, TypeTag enclosingType, boolean shortCircuitRecursiveTypeBound) {
        List<TypeTag> nestedTags = new ArrayList<>();
        if (type instanceof Class) {
            return new TypeTag((Class<?>)type, nestedTags);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            Type[] typeArgs = pt.getActualTypeArguments();
            for (Type typeArg : typeArgs) {
                nestedTags.add(resolve(typeArg, enclosingType, shortCircuitRecursiveTypeBound));
            }
            return new TypeTag((Class<?>)pt.getRawType(), nestedTags);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)type;
            TypeTag tag = resolve(gat.getGenericComponentType(), enclosingType, false);
            String arrayTypeName = "[L" + tag.getType().getName() + ";";
            Class<?> arrayType = classForName(arrayTypeName);
            return new TypeTag(arrayType, tag.getGenericTypes());
        }
        if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType)type;
            for (Type b : wt.getLowerBounds()) {
                return resolve(b, enclosingType, false);
            }
            for (Type b : wt.getUpperBounds()) {
                return resolve(b, enclosingType, false);
            }
            return new TypeTag(Object.class);
        }
        if (type instanceof java.lang.reflect.TypeVariable) {
            java.lang.reflect.TypeVariable<?> tvt = (java.lang.reflect.TypeVariable<?>)type;
            Map<String, TypeTag> typeVariableLookup = buildLookup(enclosingType);
            String typeVariableName = tvt.getName();
            if (typeVariableLookup.containsKey(typeVariableName)) {
                return typeVariableLookup.get(typeVariableName);
            }
            for (Type b : tvt.getBounds()) {
                if (!shortCircuitRecursiveTypeBound) {
                    return resolve(b, enclosingType, true);
                }
            }
            return new TypeTag(Object.class);
        }
        throw new EqualsVerifierBugException("Failed to tag type " + type.toString() + " (" + type.getClass() + ")");
    }

    private static Map<String, TypeTag> buildLookup(TypeTag enclosingType) {
        java.lang.reflect.TypeVariable<?>[] typeParameters = enclosingType.getType().getTypeParameters();
        Map<String, TypeTag> lookup = new HashMap<>();
        if (enclosingType.getGenericTypes().size() == 0) {
            return lookup;
        }

        for (int i = 0; i < typeParameters.length; i++) {
            String name = typeParameters[i].getName();
            TypeTag tag = enclosingType.getGenericTypes().get(i);
            lookup.put(name, tag);
        }
        return lookup;
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + type.hashCode();
        result = (59 * result) + genericTypes.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
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

    private static final class NullType {}
}
