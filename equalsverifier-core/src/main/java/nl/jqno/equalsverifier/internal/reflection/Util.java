package nl.jqno.equalsverifier.internal.reflection;

public final class Util {

    private Util() {
        // Do not instantiate
    }

    /**
     * Helper method to resolve a Class of a given name.
     *
     * @param className The fully qualified name of the class to resolve.
     * @param <T>       The type of the class to resolve.
     * @return The corresponding class if it exists, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(String className) {
        try {
            return (Class<T>) Class.forName(className);
        }
        catch (ClassNotFoundException | VerifyError e) {
            // Catching VerifyError fixes issue #147. I don't know how to unit test it.
            return null;
        }
    }

    /**
     * Helper method to resolve a Class of a given name through a given ClassLoader.
     *
     * @param classLoader The class loader to resolve the class against.
     * @param className   The fully qualified name of the class to resolve.
     * @param <T>         The type of the class to resolve.
     * @return The corresponding class if it exists, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(ClassLoader classLoader, String className) {
        try {
            return (Class<T>) classLoader.loadClass(className);
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
}
