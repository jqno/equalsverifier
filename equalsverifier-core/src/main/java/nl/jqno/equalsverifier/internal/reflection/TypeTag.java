package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;

import java.lang.reflect.*;
import java.util.*;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;

/**
 * Represents a generic type, including raw type and generic type parameters.
 *
 * <p>
 * If the type is not generic, the genericTypes list will be empty.
 */
public final record TypeTag(Class<?> type, List<TypeTag> genericTypes) {

    /** Null object for TypeTag. */
    public static final TypeTag NULL = new TypeTag(NullType.class);

    /**
     * Constructor.
     *
     * @param type         The raw type.
     * @param genericTypes A list of TypeTags for each generic type parameter.
     */
    public TypeTag(Class<?> type, TypeTag... genericTypes) {
        this(type, Arrays.asList(genericTypes));
    }

    /**
     * Constructor.
     *
     * @param type         The raw type.
     * @param genericTypes A list of TypeTags for each generic type parameter.
     */
    public TypeTag(Class<?> type, List<TypeTag> genericTypes) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.type = type;
        this.genericTypes = Collections.unmodifiableList(genericTypes);
    }

    /**
     * Resolves a TypeTag from the type of a {@link Field} instance, using an enclosing type to determine any generic
     * parameters the field may contain.
     *
     * @param field         The field to resolve.
     * @param enclosingType The type that contains the field, used to determine any generic parameters it may contain.
     * @return The TypeTag for the given field.
     */
    public static TypeTag of(Field field, TypeTag enclosingType) {
        return resolve(field.getGenericType(), field.getType(), enclosingType, false);
    }

    private static TypeTag resolve(
            Type type,
            Class<?> typeAsClass,
            TypeTag enclosingType,
            boolean shortCircuitRecursiveTypeBound) {
        var nestedTags = new ArrayList<TypeTag>();
        if (type instanceof Class<?> cls) {
            return processClass(cls, nestedTags);
        }
        if (type instanceof ParameterizedType parameterizedType) {
            return processParameterizedType(
                parameterizedType,
                typeAsClass,
                enclosingType,
                nestedTags,
                shortCircuitRecursiveTypeBound);
        }
        if (type instanceof GenericArrayType arrayType) {
            return processGenericArray(arrayType, typeAsClass, enclosingType);
        }
        if (type instanceof WildcardType wildcardType) {
            return processWildcard(wildcardType, typeAsClass, enclosingType, shortCircuitRecursiveTypeBound);
        }
        if (type instanceof TypeVariable<?> variable) {
            return processTypeVariable(variable, typeAsClass, enclosingType, shortCircuitRecursiveTypeBound);
        }
        throw new EqualsVerifierInternalBugException(
                "Failed to tag type " + type.toString() + " (" + type.getClass() + ")");
    }

    private static TypeTag processClass(Class<?> type, List<TypeTag> nestedTags) {
        return new TypeTag(type, nestedTags);
    }

    private static TypeTag processParameterizedType(
            ParameterizedType type,
            Class<?> typeAsClass,
            TypeTag enclosingType,
            List<TypeTag> nestedTags,
            boolean shortCircuitRecursiveTypeBound) {
        Type[] typeArgs = type.getActualTypeArguments();
        for (Type typeArg : typeArgs) {
            nestedTags.add(resolve(typeArg, typeAsClass, enclosingType, shortCircuitRecursiveTypeBound));
        }
        return new TypeTag((Class<?>) type.getRawType(), nestedTags);
    }

    private static TypeTag processGenericArray(GenericArrayType type, Class<?> typeAsClass, TypeTag enclosingType) {
        TypeTag tag = resolve(type.getGenericComponentType(), typeAsClass, enclosingType, false);
        String arrayTypeName = "[L" + tag.getType().getName() + ";";
        Class<?> arrayType = classForName(arrayTypeName);
        return new TypeTag(arrayType, tag.genericTypes());
    }

    private static TypeTag processWildcard(
            WildcardType type,
            Class<?> typeAsClass,
            TypeTag enclosingType,
            boolean shortCircuitRecursiveTypeBound) {
        for (Type b : type.getLowerBounds()) {
            return resolve(b, typeAsClass, enclosingType, shortCircuitRecursiveTypeBound);
        }
        for (Type b : type.getUpperBounds()) {
            TypeTag upper = resolve(b, typeAsClass, enclosingType, shortCircuitRecursiveTypeBound);
            if (!Object.class.equals(upper.getType())) {
                return upper;
            }
        }
        for (TypeVariable<?> tv : typeAsClass.getTypeParameters()) {
            for (Type b : tv.getBounds()) {
                return resolve(b, typeAsClass, enclosingType, shortCircuitRecursiveTypeBound);
            }
        }
        return new TypeTag(Object.class);
    }

    private static TypeTag processTypeVariable(
            TypeVariable<?> type,
            Class<?> typeAsClass,
            TypeTag enclosingType,
            boolean shortCircuitRecursiveTypeBound) {
        Map<String, TypeTag> typeVariableLookup = buildLookup(enclosingType);
        String typeVariableName = type.getName();
        if (typeVariableLookup.containsKey(typeVariableName)) {
            return typeVariableLookup.get(typeVariableName);
        }
        for (Type b : type.getBounds()) {
            if (!shortCircuitRecursiveTypeBound) {
                return resolve(b, typeAsClass, enclosingType, true);
            }
        }
        return new TypeTag(Object.class);
    }

    private static Map<String, TypeTag> buildLookup(TypeTag enclosingType) {
        TypeVariable<?>[] typeParameters = enclosingType.getType().getTypeParameters();
        if (enclosingType.genericTypes().size() == 0) {
            return Map.of();
        }

        var lookup = new HashMap<String, TypeTag>();
        for (int i = 0; i < typeParameters.length; i++) {
            String name = typeParameters[i].getName();
            TypeTag tag = enclosingType.genericTypes().get(i);
            lookup.put(name, tag);
        }
        return lookup;
    }

    /**
     * @param <T> The returned {@link Class} will have this generic type.
     * @return The TypeTag's raw type.
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getType() {
        return (Class<T>) type;
    }

    /** {@inheritDoc} */
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

    private static final class NullType {}
}
