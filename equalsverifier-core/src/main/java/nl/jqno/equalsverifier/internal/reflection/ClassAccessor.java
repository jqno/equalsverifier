package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/**
 * Instantiates and populates objects of a given class. {@link ClassAccessor} can create two
 * different instances of T, which are guaranteed not to be equal to each other, and which contain
 * no null values.
 *
 * @param <T> A class.
 */
public class ClassAccessor<T> {

    private final Class<T> type;
    private final PrefabValues prefabValues;

    /** Private constructor. Call {@link #of(Class, PrefabValues)} instead. */
    ClassAccessor(Class<T> type, PrefabValues prefabValues) {
        this.type = type;
        this.prefabValues = prefabValues;
    }

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link ClassAccessor} operates.
     * @param type The class on which {@link ClassAccessor} operates. Should be the same as T.
     * @param prefabValues Prefabricated values with which to fill instantiated objects.
     * @return A {@link ClassAccessor} for T.
     */
    public static <T> ClassAccessor<T> of(Class<T> type, PrefabValues prefabValues) {
        return new ClassAccessor<>(type, prefabValues);
    }

    /** @return The class on which {@link ClassAccessor} operates. */
    public Class<T> getType() {
        return type;
    }

    /**
     * Determines whether T is a Java Record.
     *
     * @return true if T is a Java Record.
     */
    public boolean isRecord() {
        return RecordsHelper.isRecord(type);
    }

    /**
     * Determines whether T is a sealed class.
     *
     * @return true if T is a sealed class
     */
    public boolean isSealed() {
        return SealedTypesHelper.isSealed(type);
    }

    /**
     * Determines whether T declares a field. This does not include inherited fields.
     *
     * @param field The field that we want to detect.
     * @return True if T declares the field.
     */
    public boolean declaresField(Field field) {
        try {
            type.getDeclaredField(field.getName());
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Determines whether T has an {@code equals} method.
     *
     * @return True if T has an {@code equals} method.
     */
    public boolean declaresEquals() {
        return declaresMethod(type, "equals", Object.class);
    }

    /**
     * Determines whether T has an {@code hashCode} method.
     *
     * @return True if T has an {@code hashCode} method.
     */
    public boolean declaresHashCode() {
        return declaresMethod(type, "hashCode");
    }

    /**
     * Determines whether T has a method with the given name and parameters.
     *
     * @param name The name of the method we're looking for.
     * @return True if T has a method with the given name and parameters.
     */
    public boolean hasMethod(String name) {
        Class<?> t = type;
        while (t != null) {
            if (declaresMethod(t, name)) {
                return true;
            }
            t = t.getSuperclass();
        }
        return false;
    }

    private static boolean declaresMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            type.getDeclaredMethod(name, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Determines whether T's {@code equals} method is abstract.
     *
     * @return True if T's {@code equals} method is abstract.
     */
    public boolean isEqualsAbstract() {
        return isMethodAbstract("equals", Object.class);
    }

    /**
     * Determines whether T's {@code hashCode} method is abstract.
     *
     * @return True if T's {@code hashCode} method is abstract.
     */
    public boolean isHashCodeAbstract() {
        return isMethodAbstract("hashCode");
    }

    private boolean isMethodAbstract(String name, Class<?>... parameterTypes) {
        return rethrow(() ->
            Modifier.isAbstract(type.getMethod(name, parameterTypes).getModifiers())
        );
    }

    /**
     * Determines whether T's {@code equals} method is inherited from {@link Object}.
     *
     * @return true if T's {@code equals} method is inherited from {@link Object}; false if it is
     *     overridden in T or in any of its superclasses (except {@link Object}).
     */
    public boolean isEqualsInheritedFromObject() {
        ClassAccessor<? super T> i = this;
        while (i.getType() != Object.class) {
            if (i.declaresEquals() && !i.isEqualsAbstract()) {
                return false;
            }
            i = i.getSuperAccessor();
        }
        return true;
    }

    /**
     * Returns an accessor for T's superclass.
     *
     * @return An accessor for T's superclass.
     */
    public ClassAccessor<? super T> getSuperAccessor() {
        return ClassAccessor.of(type.getSuperclass(), prefabValues);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getBlueObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An instance of T.
     */
    public T getRedObject(TypeTag enclosingType) {
        return getRedAccessor(enclosingType).get();
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getBlueObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An instance of T.
     */
    public T getRedObject(TypeTag enclosingType, LinkedHashSet<TypeTag> typeStack) {
        return getRedAccessor(enclosingType, typeStack).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     */
    public ObjectAccessor<T> getRedAccessor(TypeTag enclosingType) {
        return getRedAccessor(enclosingType, new LinkedHashSet<>());
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     */
    public ObjectAccessor<T> getRedAccessor(
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return buildObjectAccessor().scramble(prefabValues, enclosingType, typeStack);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An instance of T.
     */
    public T getBlueObject(TypeTag enclosingType) {
        return getBlueAccessor(enclosingType).get();
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned by {@link
     * #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An instance of T.
     */
    public T getBlueObject(TypeTag enclosingType, LinkedHashSet<TypeTag> typeStack) {
        return getBlueAccessor(enclosingType, typeStack).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlueObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @return An {@link ObjectAccessor} for {@link #getBlueObject(TypeTag)}.
     */
    public ObjectAccessor<T> getBlueAccessor(TypeTag enclosingType) {
        return getBlueAccessor(enclosingType, new LinkedHashSet<>());
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlueObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a field, to determine
     *     any generic parameters it may contain.
     * @param typeStack Keeps track of recursion in the type.
     * @return An {@link ObjectAccessor} for {@link #getBlueObject(TypeTag)}.
     */
    public ObjectAccessor<T> getBlueAccessor(
        TypeTag enclosingType,
        LinkedHashSet<TypeTag> typeStack
    ) {
        return buildObjectAccessor()
            .scramble(prefabValues, enclosingType, typeStack)
            .scramble(prefabValues, enclosingType, typeStack);
    }

    private ObjectAccessor<T> buildObjectAccessor() {
        T object = Instantiator.of(type).instantiate();
        return ObjectAccessor.of(object);
    }
}
