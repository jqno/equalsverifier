package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import nl.jqno.equalsverifier.internal.util.ObjenesisWrapper;

/**
 * Instantiates objects of a given class.
 *
 * @param <T> {@link Instantiator} instantiates objects of this class, or of an anonymous subclass
 *     of this class.
 */
public final class Instantiator<T> {

    private static final List<String> FORBIDDEN_PACKAGES = Arrays.asList(
        "java.",
        "javax.",
        "sun.",
        "com.sun.",
        "org.w3c.dom."
    );
    private static final String FALLBACK_PACKAGE_NAME = getPackageName(Instantiator.class);

    private final Class<T> type;

    /** Private constructor. Call {@link #of(Class)} to instantiate. */
    private Instantiator(Class<T> type) {
        this.type = type;
    }

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link Instantiator} operates.
     * @param type The class on which {@link Instantiator} operates. Should be the same as T.
     * @return An {@link Instantiator} for {@link #type}.
     */
    public static <T> Instantiator<T> of(Class<T> type) {
        if (Modifier.isAbstract(type.getModifiers())) {
            return new Instantiator<>(giveDynamicSubclass(type));
        }
        return new Instantiator<>(type);
    }

    /**
     * Instantiates an object of type T.
     *
     * <p>All fields will be initialized to their initial values. I.e., 0 for ints, null for
     * objects, etc.
     *
     * @return An object of type T.
     */
    public T instantiate() {
        return ObjenesisWrapper.getObjenesis().newInstance(type);
    }

    /**
     * Instantiates an anonymous subclass of T. The subclass is generated dynamically.
     *
     * @return An instance of an anonymous subclass of T.
     */
    public T instantiateAnonymousSubclass() {
        Class<T> proxyClass = giveDynamicSubclass(type);
        return ObjenesisWrapper.getObjenesis().newInstance(proxyClass);
    }

    @SuppressWarnings("unchecked")
    private static synchronized <S> Class<S> giveDynamicSubclass(Class<S> superclass) {
        boolean isSystemClass = isSystemClass(superclass.getName());

        String namePrefix = isSystemClass ? FALLBACK_PACKAGE_NAME : getPackageName(superclass);
        String name =
            namePrefix +
            "." +
            superclass.getSimpleName() +
            "$$DynamicSubclass$" +
            superclass.hashCode();

        Class<S> existsAlready = (Class<S>) classForName(name);
        if (existsAlready != null) {
            return existsAlready;
        }

        Class<?> context = isSystemClass ? Instantiator.class : superclass;
        ClassLoadingStrategy<? super ClassLoader> cs = getClassLoadingStrategy(context);
        return (Class<S>) new ByteBuddy()
            .with(TypeValidation.DISABLED)
            .subclass(superclass)
            .name(name)
            .make()
            .load(context.getClassLoader(), cs)
            .getLoaded();
    }

    private static String getPackageName(Class<?> type) {
        String cn = type.getName();
        int dot = cn.lastIndexOf('.');
        return (dot != -1) ? cn.substring(0, dot).intern() : "";
    }

    @SuppressWarnings("unchecked")
    public static <S> ClassLoadingStrategy<? super ClassLoader> getClassLoadingStrategy(
        Class<S> context
    ) {
        if (System.getProperty("java.version").startsWith("1.")) {
            return ClassLoadingStrategy.Default.INJECTION.with(context.getProtectionDomain());
        } else {
            ConditionalInstantiator ci = new ConditionalInstantiator(
                "java.lang.invoke.MethodHandles$Lookup"
            );
            Object lookup = ci.callFactory(
                "java.lang.invoke.MethodHandles",
                "privateLookupIn",
                classes(Class.class, MethodHandles.Lookup.class),
                objects(context, MethodHandles.lookup())
            );
            return ClassLoadingStrategy.UsingLookup.of(lookup);
        }
    }

    private static boolean isSystemClass(String className) {
        for (String prefix : FORBIDDEN_PACKAGES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
