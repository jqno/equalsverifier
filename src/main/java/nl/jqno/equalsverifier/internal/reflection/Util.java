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
    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
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
     * Helper method to create an array of Objects.
     *
     * @param objects The objects to construct an array out of.
     * @return An array with the given objects.
     */
    public static Object[] objects(Object... objects) {
        return objects;
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
