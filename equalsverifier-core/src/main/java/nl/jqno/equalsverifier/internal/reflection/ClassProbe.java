package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

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

    /**
     * Returns the class on which {@link ClassProbe} operates.
     *
     * @return The class on which {@link ClassProbe} operates.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Determines whether T is a Java Record.
     *
     * @return true if T is a Java Record.
     */
    public boolean isRecord() {
        return type.isRecord();
    }

    /**
     * Determines whether T is a sealed class.
     *
     * @return true if T is a sealed class
     */
    public boolean isSealed() {
        return type.isSealed();
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
     * Determines whether T has a {@code hashCode} method.
     *
     * @return True if T has a {@code hashCode} method.
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

    @SuppressWarnings("ReturnValueIgnored")
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
     * Finds a field (no matter its accessibility) in T or its superclasses.
     *
     * @param name The name of the field that should be found.
     * @return The field wrapped in an Optional, or an empty Optional if the field could not be found.
     */
    public Optional<Field> findField(String name) {
        Class<?> t = type;
        while (t != null) {
            Field f = getField(t, name);
            if (f != null) {
                return Optional.of(f);
            }
            t = t.getSuperclass();
        }
        return Optional.empty();
    }

    private static Field getField(Class<?> type, String name) {
        try {
            return type.getDeclaredField(name);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Determines whether the given method in T is final.
     *
     * @param methodName     The name of the method in T.
     * @param parameterTypes The types of the method's parameters, to determine the right overload.
     * @return True if the given method in T is final, false if it's not final or if it doesn't exist.
     */
    public boolean isMethodFinal(String methodName, Class<?>... parameterTypes) {
        return findMethod(methodName, parameterTypes).map(m -> Modifier.isFinal(m.getModifiers())).orElse(false);
    }

    private Optional<Method> findMethod(String name, Class<?>... parameterTypes) {
        Class<?> t = type;
        while (t != null) {
            Method f = getMethod(t, name, parameterTypes);
            if (f != null) {
                return Optional.of(f);
            }
            t = t.getSuperclass();
        }
        return Optional.empty();
    }

    private static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return type.getDeclaredMethod(name, parameterTypes);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
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
