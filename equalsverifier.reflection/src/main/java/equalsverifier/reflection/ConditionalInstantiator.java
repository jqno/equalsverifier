package equalsverifier.reflection;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static equalsverifier.reflection.Util.classForName;

/**
 * Allows instantiation of classes that may or may not be present on the classpath.
 */
public class ConditionalInstantiator {
    private final String fullyQualifiedClassName;
    private final boolean throwExceptions;

    /**
     * Constructor.
     *
     * @param fullyQualifiedClassName
     *          The fully-qualified name of the class that we intend to
     *          instantiate.
     */
    public ConditionalInstantiator(String fullyQualifiedClassName) {
        this(fullyQualifiedClassName, true);
    }

    /**
     * Constructor.
     *
     * @param fullyQualifiedClassName
     *          The fully-qualified name of the class that we intend to
     *          instantiate.
     * @param throwExceptions
     *          Whether to throw exceptions when the class can't be
     *          instantiated.
     */
    public ConditionalInstantiator(String fullyQualifiedClassName, boolean throwExceptions) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.throwExceptions = throwExceptions;
    }

    /**
     * Attempts to resolve the type.
     *
     * @param <T> The resolved type.
     * @return The corresponding class object if the type exists; null otherwise.
     */
    public <T> Class<T> resolve() {
        return classForName(fullyQualifiedClassName);
    }

    /**
     * Attempts to instantiate the type.
     *
     * @param <T>
     *          The type to instantiate.
     * @param paramTypes
     *          The types of the constructor parameters of the constructor
     *          that we want to call.
     * @param paramValues
     *          The values that we want to pass into the constructor.
     * @return An instance of the type given in the constructor with the given
     *         parameter values, or null if the type does not exist.
     * @throws ReflectionException If instantiation fails.
     */
    @SuppressFBWarnings(value = "DP_DO_INSIDE_DO_PRIVILEGED", justification = "EV is run only from within unit tests")
    public <T> T instantiate(Class<?>[] paramTypes, Object[] paramValues) {
        try {
            Class<T> type = resolve();
            if (type == null) {
                return null;
            }
            Constructor<T> c = type.getConstructor(paramTypes);
            c.setAccessible(true);
            return c.newInstance(paramValues);
        }
        catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * Attempts to call a static factory method on the type.
     *
     * @param <T>
     *          The return type of the factory method.
     * @param factoryMethod
     *          The name of the factory method.
     * @param paramTypes
     *          The types of the parameters of the specific overload of the
     *          factory method we want to call.
     * @param paramValues
     *          The values that we want to pass into the factory method.
     * @return An instance of the type given by the factory method with the
     *         given parameter values, or null of the type does not exist.
     * @throws ReflectionException
     *          If the call to the factory method fails.
     */
    public <T> T callFactory(String factoryMethod, Class<?>[] paramTypes, Object[] paramValues) {
        return callFactory(fullyQualifiedClassName, factoryMethod, paramTypes, paramValues);
    }

    /**
     * Attempts to call a static factory method on a type.
     *
     * @param <T>
     *          The return type of the factory method.
     * @param factoryTypeName
     *          The type that contains the factory method.
     * @param factoryMethod
     *          The name of the factory method.
     * @param paramTypes
     *          The types of the parameters of the specific overload of the
     *          factory method we want to call.
     * @param paramValues
     *          The values that we want to pass into the factory method.
     * @return An instance of the type given by the factory method with the
     *          given parameter values, or null of the type does not exist.
     * @throws ReflectionException
     *          If the call to the factory method fails.
     */
    @SuppressFBWarnings(value = "DP_DO_INSIDE_DO_PRIVILEGED", justification = "EV is run only from within unit tests")
    @SuppressWarnings("unchecked")
    public <T> T callFactory(String factoryTypeName, String factoryMethod, Class<?>[] paramTypes, Object[] paramValues) {
        try {
            Class<T> type = resolve();
            if (type == null) {
                return null;
            }
            Class<?> factoryType = Class.forName(factoryTypeName);
            Method factory = factoryType.getMethod(factoryMethod, paramTypes);
            factory.setAccessible(true);
            return (T)factory.invoke(null, paramValues);
        }
        catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * Attempts to resolve a static constant on the type.
     *
     * @param <T>
     *          The type of the constant.
     * @param constantName
     *          The name of the constant.
     * @return The value of the constant, or null if the type does not exist.
     * @throws ReflectionException
     *          If resolving the constant fails.
     */
    @SuppressFBWarnings(value = "DP_DO_INSIDE_DO_PRIVILEGED", justification = "EV is run only from within unit tests")
    @SuppressWarnings("unchecked")
    public <T> T returnConstant(String constantName) {
        try {
            Class<T> type = resolve();
            if (type == null) {
                return null;
            }
            Field field = type.getField(constantName);
            field.setAccessible(true);
            return (T)field.get(null);
        }
        catch (Exception e) {
            return handleException(e);
        }
    }

    private <T> T handleException(Exception e) {
        if (throwExceptions) {
            throw new ReflectionException(e);
        }
        else {
            return null;
        }
    }
}
