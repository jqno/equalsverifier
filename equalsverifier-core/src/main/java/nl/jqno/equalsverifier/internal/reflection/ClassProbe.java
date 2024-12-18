package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Modifier;

import nl.jqno.equalsverifier.internal.versionspecific.RecordsHelper;
import nl.jqno.equalsverifier.internal.versionspecific.SealedTypesHelper;

/**
 * Provides read-only reflective access to a class.
 */
public final class ClassProbe<T> {

    private final Class<T> type;

    /** Private constructor. Call {@link #of(Class)} instead. */
    private ClassProbe(Class<T> type) {
        this.type = type;
    }

    /**
     * Factory method.
     *
     * @param <T>  The class on which {@link ClassProbe} operates.
     * @param type The class on which {@link ClassProbe} operates.
     * @return A {@link ClassProbe} for T.
     */
    public static <T> ClassProbe<T> of(Class<T> type) {
        return new ClassProbe<>(type);
    }

    /** @return The class on which {@link ClassProbe} operates. */
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
        return rethrow(() -> Modifier.isAbstract(type.getMethod(name, parameterTypes).getModifiers()));
    }

    /**
     * Determines whether T's {@code equals} method is inherited from {@link Object}.
     *
     * @return true if T's {@code equals} method is inherited from {@link Object}; false if it is overridden in T or in
     *             any of its superclasses (except {@link Object}).
     */
    public boolean isEqualsInheritedFromObject() {
        ClassProbe<? super T> i = this;
        while (i.getType() != Object.class) {
            if (i.declaresEquals() && !i.isEqualsAbstract()) {
                return false;
            }
            i = i.getSuperProbe();
        }
        return true;
    }

    /**
     * Returns a probe for T's superclass.
     *
     * @return A probe for T's superclass.
     */
    public ClassProbe<? super T> getSuperProbe() {
        return new ClassProbe<>(type.getSuperclass());
    }
}
