package nl.jqno.equalsverifier.internal.prefabvalues;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;

import java.lang.reflect.*;
import java.util.*;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;

/**
 * Represents a generic type, including raw type and generic type parameters.
 *
 * <p>If the type is not generic, the genericTypes list will be empty.
 */
public final class TypeTag {
    /** Null object for TypeTag. */
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
     * Resolves a TypeTag from the type of a {@link Field} instance, using an enclosing type to
     * determine any generic parameters the field may contain.
     *
     * @param field The field to resolve.
     * @param enclosingType The type that contains the field, used to determine any generic
     *     parameters it may contain.
     * @return The TypeTag for the given field.
     */
    public static TypeTag of(Field field, TypeTag enclosingType) {
        return resolve(field.getGenericType(), enclosingType, false);
    }

    private static TypeTag resolve(
            Type type, TypeTag enclosingType, boolean shortCircuitRecursiveTypeBound) {
        List<TypeTag> nestedTags = new ArrayList<>();
        if (type instanceof Class) {
            return processClass((Class<?>) type, nestedTags);
        }
        if (type instanceof ParameterizedType) {
            return processParameterizedType(
                    (ParameterizedType) type,
                    enclosingType,
                    nestedTags,
                    shortCircuitRecursiveTypeBound);
        }
        if (type instanceof GenericArrayType) {
            return processGenericArray((GenericArrayType) type, enclosingType);
        }
        if (type instanceof WildcardType) {
            return processWildcard(
                    (WildcardType) type, enclosingType, shortCircuitRecursiveTypeBound);
        }
        if (type instanceof TypeVariable) {
            return processTypeVariable(
                    (TypeVariable<?>) type, enclosingType, shortCircuitRecursiveTypeBound);
        }
        throw new EqualsVerifierInternalBugException(
                "Failed to tag type " + type.toString() + " (" + type.getClass() + ")");
    }

    private static TypeTag processClass(Class<?> type, List<TypeTag> nestedTags) {
        return new TypeTag(type, nestedTags);
    }

    private static TypeTag processParameterizedType(
            ParameterizedType type,
            TypeTag enclosingType,
            List<TypeTag> nestedTags,
            boolean shortCircuitRecursiveTypeBound) {
        Type[] typeArgs = type.getActualTypeArguments();
        for (Type typeArg : typeArgs) {
            nestedTags.add(resolve(typeArg, enclosingType, shortCircuitRecursiveTypeBound));
        }
        return new TypeTag((Class<?>) type.getRawType(), nestedTags);
    }

    private static TypeTag processGenericArray(GenericArrayType type, TypeTag enclosingType) {
        TypeTag tag = resolve(type.getGenericComponentType(), enclosingType, false);
        String arrayTypeName = "[L" + tag.getType().getName() + ";";
        Class<?> arrayType = classForName(arrayTypeName);
        return new TypeTag(arrayType, tag.getGenericTypes());
    }

    private static TypeTag processWildcard(
            WildcardType type, TypeTag enclosingType, boolean shortCircuitRecursiveTypeBound) {
        for (Type b : type.getLowerBounds()) {
            return resolve(b, enclosingType, shortCircuitRecursiveTypeBound);
        }
        for (Type b : type.getUpperBounds()) {
            return resolve(b, enclosingType, shortCircuitRecursiveTypeBound);
        }
        return new TypeTag(Object.class);
    }

    private static TypeTag processTypeVariable(
            TypeVariable<?> type, TypeTag enclosingType, boolean shortCircuitRecursiveTypeBound) {
        Map<String, TypeTag> typeVariableLookup = buildLookup(enclosingType);
        String typeVariableName = type.getName();
        if (typeVariableLookup.containsKey(typeVariableName)) {
            return typeVariableLookup.get(typeVariableName);
        }
        for (Type b : type.getBounds()) {
            if (!shortCircuitRecursiveTypeBound) {
                return resolve(b, enclosingType, true);
            }
        }
        return new TypeTag(Object.class);
    }

    private static Map<String, TypeTag> buildLookup(TypeTag enclosingType) {
        TypeVariable<?>[] typeParameters = enclosingType.getType().getTypeParameters();
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
     * @param <T> The returned {@link Class} will have this generic type.
     * @return The TypeTag's raw type.
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getType() {
        return (Class<T>) type;
    }

    /** @return The TypeTag's generic types. */
    public List<TypeTag> getGenericTypes() {
        return Collections.unmodifiableList(genericTypes);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TypeTag)) {
            return false;
        }
        TypeTag other = (TypeTag) obj;
        return type.equals(other.type) && genericTypes.equals(other.genericTypes);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = 37;
        result = (59 * result) + type.hashCode();
        result = (59 * result) + genericTypes.hashCode();
        return result;
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
