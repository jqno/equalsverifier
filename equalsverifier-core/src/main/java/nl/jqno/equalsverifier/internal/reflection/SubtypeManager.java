package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;

public final class SubtypeManager {

    private static final List<String> FORBIDDEN_PACKAGES =
            Arrays.asList("java.", "javax.", "sun.", "com.sun.", "org.w3c.dom.");
    private static final String FALLBACK_PACKAGE_NAME = getPackageName(Instantiator.class);

    private SubtypeManager() {}

    public static <T> Optional<Class<T>> findInstantiableSubclass(ClassProbe<T> probe) {
        if (probe.isSealed() && probe.isAbstract()) {
            return findInstantiablePermittedSubclass(probe);
        }
        if (probe.isAbstract()) {
            return Optional.of(giveDynamicSubclass(probe.getType()));
        }

        return Optional.of(probe.getType());
    }

    /**
     * Generates an anonymous subclass of S. The subclass is generated dynamically.
     *
     * @param <S>        The class to create a dynamic subclass of.
     * @param superclass The class to create a dynamic subclass of.
     * @return An instance of an anonymous subclass of S.
     */
    public static <S> Class<S> giveDynamicSubclass(Class<S> superclass) {
        return giveDynamicSubclass(superclass, "", b -> b);
    }

    /**
     * Generates an anonymous subclass of S. The subclass is generated dynamically.
     *
     * @param <S>        The class to create a dynamic subclass of.
     * @param superclass The class to create a dynamic subclass of.
     * @param nameSuffix A constant that will be appended to the name of the newly generated class.
     * @param modify     Allows custom modifications to the generated class.
     * @return An instance of an anonymous subclass of S.
     */
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

    @SuppressWarnings("unchecked")
    private static <T> Optional<Class<T>> findInstantiablePermittedSubclass(ClassProbe<T> probe) {
        return findInstantiablePermittedSubclasses(probe).findFirst().map(c -> (Class<T>) c);
    }

    public static <T> Stream<Class<? extends T>> findInstantiablePermittedSubclasses(ClassProbe<T> probe) {
        if (!probe.isAbstract() || !probe.isSealed()) {
            return Stream.of(probe.getType());
        }

        var permittedSubclasses = probe.getType().getPermittedSubclasses();
        if (permittedSubclasses == null) {
            return Stream.empty();
        }

        return Arrays.stream(permittedSubclasses).flatMap(permitted -> {
            @SuppressWarnings("unchecked")
            ClassProbe<T> subProbe = (ClassProbe<T>) ClassProbe.of(permitted);
            return findInstantiablePermittedSubclasses(subProbe);
        });
    }

    private static <S> ClassLoadingStrategy<ClassLoader> getClassLoadingStrategy(Class<S> context) {
        try {
            var lookup = MethodHandles.privateLookupIn(context, MethodHandles.lookup());
            return ClassLoadingStrategy.UsingLookup.of(lookup);
        }
        catch (IllegalAccessException e) {
            return ClassLoadingStrategy.Default.INJECTION.with(context.getProtectionDomain());
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

    private static String getPackageName(Class<?> type) {
        String cn = type.getName();
        int dot = cn.lastIndexOf('.');
        return dot != -1 ? cn.substring(0, dot).intern() : "";
    }
}
