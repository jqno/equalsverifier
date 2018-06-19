package nl.jqno.equalsverifier.internal.reflection;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.annotations.Annotation;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.NonnullAnnotationVerifier;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Instantiates and populates objects of a given class. {@link ClassAccessor}
 * can create two different instances of T, which are guaranteed not to be
 * equal to each other, and which contain no null values.
 *
 * @param <T> A class.
 */
public class ClassAccessor<T> {
    private static final Map<Map.Entry<String, Annotation>, Boolean> PACKAGE_ANNOTATION_CACHE = Util.newLruCache(512);
    private final Class<T> type;
    private final PrefabValues prefabValues;
    private final Annotation[] supportedAnnotations;
    private final Set<String> ignoredAnnotations;
    private final boolean ignoreAnnotationFailure;
    private final AnnotationAccessor annotationAccessor;

    /**
     * Private constructor. Call {@link #of(Class, PrefabValues, Set, boolean)} instead.
     */
    ClassAccessor(Class<T> type, PrefabValues prefabValues, Annotation[] supportedAnnotations,
            Set<String> ignoredAnnotations, boolean ignoreAnnotationFailure) {
        this.type = type;
        this.prefabValues = prefabValues;
        this.supportedAnnotations = supportedAnnotations;
        this.ignoredAnnotations = ignoredAnnotations;
        this.ignoreAnnotationFailure = ignoreAnnotationFailure;
        this.annotationAccessor = new AnnotationAccessor(supportedAnnotations, type, ignoredAnnotations, ignoreAnnotationFailure);
    }

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link ClassAccessor} operates.
     * @param type The class on which {@link ClassAccessor} operates. Should be
     *          the same as T.
     * @param prefabValues Prefabricated values with which to fill instantiated
     *          objects.
     * @param ignoredAnnotations A collection of type descriptors for
     *          annotations to ignore.
     * @param ignoreAnnotationFailure Ignore when processing annotations fails.
     * @return A {@link ClassAccessor} for T.
     */
    public static <T> ClassAccessor<T> of(Class<T> type, PrefabValues prefabValues,
            Set<String> ignoredAnnotations, boolean ignoreAnnotationFailure) {
        return new ClassAccessor<>(type, prefabValues, SupportedAnnotations.values(), ignoredAnnotations, ignoreAnnotationFailure);
    }

    /**
     * Getter.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Determines whether T has a particular annotation.
     *
     * @param annotation The annotation we want to find.
     * @return True if T has the specified annotation.
     */
    public boolean hasAnnotation(Annotation annotation) {
        return annotationAccessor.typeHas(annotation);
    }

    /**
     * Determines whether any of T's outer classes, if they exist, have a particular annotation.
     *
     * @param annotation The annotation we want to find.
     * @return True if T has an outer class with the specified annotation.
     */
    public boolean outerClassHasAnnotation(Annotation annotation) {
        Class<?> outer = type.getDeclaringClass();
        while (outer != null) {
            AnnotationAccessor accessor = new AnnotationAccessor(supportedAnnotations, outer, ignoredAnnotations, ignoreAnnotationFailure);
            if (accessor.typeHas(annotation)) {
                return true;
            }
            outer = outer.getDeclaringClass();
        }
        return false;
    }

    /**
     * Determines whether the package in which T resides has a particular annotation.
     *
     * @param annotation The annotation we want to find.
     * @return True if the package in which T resides has the specified annotation.
     */
    public boolean packageHasAnnotation(Annotation annotation) {
        try {
            Package pkg = type.getPackage();
            if (pkg == null) {
                return false;
            }

            String packageName = pkg.getName();
            Map.Entry<String, Annotation> entry = new SimpleImmutableEntry<>(packageName, annotation);
            Boolean hasAnnotation = PACKAGE_ANNOTATION_CACHE.get(entry);
            if (hasAnnotation != null) {
                return hasAnnotation;
            }

            String className = packageName + ".package-info";
            Class<?> packageType = Class.forName(className);
            AnnotationAccessor accessor = new AnnotationAccessor(supportedAnnotations, packageType, ignoredAnnotations, ignoreAnnotationFailure);
            hasAnnotation = accessor.typeHas(annotation);
            PACKAGE_ANNOTATION_CACHE.put(entry, hasAnnotation);
            return hasAnnotation;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Determines whether a particular field in T has a particular annotation.
     *
     * @param field The field for which we want to know if it has the specified
     *          annotation.
     * @param annotation The annotation we want to find.
     * @return True if the specified field in T has the specified annotation.
     */
    public boolean fieldHasAnnotation(Field field, Annotation annotation) {
        return annotationAccessor.fieldHas(field.getName(), annotation);
    }

    /**
     * Determines whether T declares a field.  This does not include inherited fields.
     *
     * @return True if T declares the field.
     */
    public boolean declaresField(Field field) {
        try {
            type.getDeclaredField(field.getName());
            return true;
        }
        catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Determines whether T has an {@code equals} method.
     *
     * @return True if T has an {@code equals} method.
     */
    public boolean declaresEquals() {
        return declaresMethod("equals", Object.class);
    }

    /**
     * Determines whether T has an {@code hashCode} method.
     *
     * @return True if T has an {@code hashCode} method.
     */
    public boolean declaresHashCode() {
        return declaresMethod("hashCode");
    }

    private boolean declaresMethod(String name, Class<?>... parameterTypes) {
        try {
            type.getDeclaredMethod(name, parameterTypes);
            return true;
        }
        catch (NoSuchMethodException e) {
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
        try {
            return Modifier.isAbstract(type.getMethod(name, parameterTypes).getModifiers());
        }
        catch (NoSuchMethodException e) {
            throw new ReflectionException("Should never occur (famous last words)");
        }
    }

    /**
     * Determines whether T's {@code equals} method is inherited from
     * {@link Object}.
     *
     * @return true if T's {@code equals} method is inherited from
     *          {@link Object}; false if it is overridden in T or in any of its
     *          superclasses (except {@link Object}).
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
        return ClassAccessor.of(type.getSuperclass(), prefabValues, ignoredAnnotations, ignoreAnnotationFailure);
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned
     * by {@link #getBlackObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     * @return An instance of T.
     */
    public T getRedObject(TypeTag enclosingType) {
        return getRedAccessor(enclosingType).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     * @return An {@link ObjectAccessor} for {@link #getRedObject(TypeTag)}.
     */
    public ObjectAccessor<T> getRedAccessor(TypeTag enclosingType) {
        ObjectAccessor<T> result = buildObjectAccessor();
        result.scramble(prefabValues, enclosingType);
        return result;
    }

    /**
     * Returns an instance of T that is not equal to the instance of T returned
     * by {@link #getRedObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     * @return An instance of T.
     */
    public T getBlackObject(TypeTag enclosingType) {
        return getBlackAccessor(enclosingType).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for {@link #getBlackObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     * @return An {@link ObjectAccessor} for {@link #getBlackObject(TypeTag)}.
     */
    public ObjectAccessor<T> getBlackAccessor(TypeTag enclosingType) {
        ObjectAccessor<T> result = buildObjectAccessor();
        result.scramble(prefabValues, enclosingType);
        result.scramble(prefabValues, enclosingType);
        return result;
    }

    /**
     * Returns an instance of T where all the fields are initialized to their
     * default values. I.e., 0 for ints, and null for objects (except when the
     * field is marked with a NonNull annotation).
     *
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     * @return An instance of T where all the fields are initialized to their
     *          default values.
     */
    public T getDefaultValuesObject(TypeTag enclosingType) {
        return getDefaultValuesAccessor(enclosingType, new HashSet<String>()).get();
    }

    /**
     * Returns an {@link ObjectAccessor} for
     * {@link #getDefaultValuesObject(TypeTag)}.
     *
     * @param enclosingType Describes the type that contains this object as a
     *                      field, to determine any generic parameters it may
     *                      contain.
     * @param nonnullFields Fields which are not allowed to be set to null.
     * @return An {@link ObjectAccessor} for
     *          {@link #getDefaultValuesObject(TypeTag)}.
     */
    public ObjectAccessor<T> getDefaultValuesAccessor(TypeTag enclosingType, Set<String> nonnullFields) {
        ObjectAccessor<T> result = buildObjectAccessor();
        for (Field field : FieldIterable.of(type)) {
            if (NonnullAnnotationVerifier.fieldIsNonnull(this, field) || nonnullFields.contains(field.getName())) {
                FieldAccessor accessor = result.fieldAccessorFor(field);
                accessor.changeField(prefabValues, enclosingType);
            }
        }
        return result;
    }

    private ObjectAccessor<T> buildObjectAccessor() {
        T object = Instantiator.of(type).instantiate();
        return ObjectAccessor.of(object);
    }
}
