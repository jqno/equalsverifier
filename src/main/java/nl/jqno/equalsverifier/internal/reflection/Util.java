package nl.jqno.equalsverifier.internal.reflection;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Util {
    private Util() {
        // Do not instantiate
    }

    /**
     * Helper method to resolve a Class of a given name.
     *
     * @param className The fully qualified name of the class to resolve.
     * @return The corresponding class if it exists, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(String className) {
        try {
            return (Class<T>)Class.forName(className);
        }
        catch (ClassNotFoundException | VerifyError e) {
            // Catching VerifyError fixes issue #147. I don't know how to unit test it.
            return null;
        }
    }

    /**
     * Helper method to create an array of Classes.
     *
     * @param classes The classes to construct an array out of.
     * @return An array with the given classes.
     */
    public static Class<?>[] classes(Class<?>... classes) {
        return classes;
    }

    /**
     * Helper method to create an empty array of Objects.
     *
     * @return An empty array.
     */
    public static Object[] objects() {
        return new Object[] {};
    }

    /**
     * Helper method to create an array of Objects.
     *
     * @param first The object to construct an array out of.
     * @return An array with the given object.
     */
    public static Object[] objects(Object first) {
        return new Object[] { first };
    }

    /**
     * Helper method to create an array of Objects.
     *
     * @param first The first object to construct an array out of.
     * @param second The second object in the array.
     * @return An array with the given objects.
     */
    public static Object[] objects(Object first, Object second) {
        return new Object[] { first, second };
    }

    /**
     * Helper method to create an array of Objects.
     *
     * @param first The first object to construct an array out of.
     * @param second The second object in the array.
     * @param third The third object in the array.
     * @return An array with the given objects.
     */
    public static Object[] objects(Object first, Object second, Object third) {
        return new Object[] { first, second, third };
    }

    /**
     * Helper method to create simple LRU cache implementation.
     *
     * @param maxSize maximum size of map
     * @return simple thread-safe map with maximum-size eviction by LRU algorithm
     */
    public static <K, V> Map<K, V> newLruCache(final int maxSize) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
        return Collections.synchronizedMap(map);
    }
}
