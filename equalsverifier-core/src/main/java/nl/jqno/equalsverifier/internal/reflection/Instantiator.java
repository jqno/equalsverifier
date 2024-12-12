package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;
import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import org.objenesis.Objenesis;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates objects of a given class.
 *
 * @param <T> {@link Instantiator} instantiates objects of this class, or of an anonymous subclass of this class.
 */
public final class Instantiator<T> {

    private static final List<String> FORBIDDEN_PACKAGES =
            Arrays.asList("java.", "javax.", "sun.", "com.sun.", "org.w3c.dom.");
    private static final String FALLBACK_PACKAGE_NAME = getPackageName(Instantiator.class);

    private final Class<T> type;
    private final Objenesis objenesis;
    private final ObjectInstantiator<T> objenesisInstantiator;

    /** Private constructor. Call {@link #of(Class)} to instantiate. */
    private Instantiator(Class<T> type, Objenesis objenesis) {
        this.type = type;
        this.objenesis = objenesis;
        this.objenesisInstantiator = objenesis.getInstantiatorOf(type);
    }

    /**
     * Factory method.
     *
     * @param <T>       The class on which {@link Instantiator} operates.
     * @param type      The class on which {@link Instantiator} operates. Should be the same as T.
     * @param objenesis To instantiate non-record classes.
     * @return An {@link Instantiator} for {@link #type}.
     */
    public static <T> Instantiator<T> of(Class<T> type, Objenesis objenesis) {
        if (SealedTypesHelper.isSealed(type)) {
            Class<T> concrete = SealedTypesHelper.findInstantiableSubclass(type).get();
            return Instantiator.of(concrete, objenesis);
        }
        if (Modifier.isAbstract(type.getModifiers())) {
            return new Instantiator<>(giveDynamicSubclass(type, "", b -> b), objenesis);
        }
        return new Instantiator<>(type, objenesis);
    }

    /**
     * Instantiates an object of type T.
     *
     * <p>
     * All fields will be initialized to their initial values. I.e., 0 for ints, null for objects, etc.
     *
     * @return An object of type T.
     */
    public T instantiate() {
        return objenesisInstantiator.newInstance();
    }

    /**
     * Instantiates an anonymous subclass of T. The subclass is generated dynamically.
     *
     * @return An instance of an anonymous subclass of T.
     */
    public T instantiateAnonymousSubclass() {
        Class<T> proxyClass = giveDynamicSubclass(type);
        return objenesis.newInstance(proxyClass);
    }

    public static <S> Class<S> giveDynamicSubclass(Class<S> superclass) {
        return giveDynamicSubclass(superclass, "", b -> b);
    }

    @SuppressWarnings("unchecked")
    public static synchronized <S> Class<S> giveDynamicSubclass(
            Class<S> superclass,
            String nameSuffix,
            UnaryOperator<DynamicType.Builder<S>> modify) {
        boolean isSystemClass = isSystemClass(superclass.getName());

        String namePrefix = isSystemClass ? FALLBACK_PACKAGE_NAME : getPackageName(superclass);
        String name = namePrefix + (namePrefix.isEmpty() ? "" : ".") + superclass.getSimpleName() + "$$DynamicSubclass$"
                + Integer.toHexString(superclass.hashCode()) + "$" + nameSuffix;

        Class<?> context = isSystemClass ? Instantiator.class : superclass;
        ClassLoader classLoader = context.getClassLoader();

        // `mvn quarkus:dev` does strange classloader stuff. We need to make sure that we
        // check existence with the correct classloader. I don't know how to unit test this.
        Class<S> existsAlready = (Class<S>) classForName(classLoader, name);
        if (existsAlready != null) {
            return existsAlready;
        }

        ClassLoadingStrategy<ClassLoader> cs = getClassLoadingStrategy(context);
        DynamicType.Builder<S> builder = new ByteBuddy().with(TypeValidation.DISABLED).subclass(superclass).name(name);

        builder = modify.apply(builder);

        return (Class<S>) builder.make().load(classLoader, cs).getLoaded();
    }

    private static String getPackageName(Class<?> type) {
        String cn = type.getName();
        int dot = cn.lastIndexOf('.');
        return dot != -1 ? cn.substring(0, dot).intern() : "";
    }

    private static <S> ClassLoadingStrategy<ClassLoader> getClassLoadingStrategy(Class<S> context) {
        if (System.getProperty("java.version").startsWith("1.")) {
            return ClassLoadingStrategy.Default.INJECTION.with(context.getProtectionDomain());
        }
        else {
            ConditionalInstantiator ci = new ConditionalInstantiator("java.lang.invoke.MethodHandles$Lookup");
            Object lookup = ci
                    .callFactory(
                        "java.lang.invoke.MethodHandles",
                        "privateLookupIn",
                        classes(Class.class, MethodHandles.Lookup.class),
                        objects(context, MethodHandles.lookup()));
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
