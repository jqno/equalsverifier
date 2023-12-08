package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class ExperimentalInstantiator {

    public final Map<String, Class<?>> cache = new HashMap<>();

    public static final String SYNTHETIC_FIELD_NAME = "$$EqualsVerifier$id";
    private final ByteBuddy bytebuddy = new ByteBuddy();
    private final ByteBuddyClassLoader bytebuddyCL;

    public ExperimentalInstantiator(ClassLoader parentClassLoader) {
        this.bytebuddyCL = new ByteBuddyClassLoader(parentClassLoader, cache);
    }

    public Class<?> lobotomize(Class<?> originalClass) {
        boolean isFinal = Modifier.isFinal(originalClass.getModifiers());
        boolean isSystem = isSystemClass(originalClass);
        if (originalClass.isEnum()) {
            return reload(originalClass);
        }
        if (originalClass.isInterface()) {
            return subclassClass(originalClass);
        }
        if (!isSystem) {
            return redefineClass(originalClass);
        } else {
            if (isFinal) {
                System.out.println("Can't do it: " + originalClass.getName());
                return null;
            }
            return subclassClass(originalClass);
        }
    }

    public <T> Class<T> reloadClass(Class<T> type) {
        reloadOuterClassOf(type);
        reloadSuperClassOf(type);
        return reload(type);
    }

    private Class<?> redefineClass(Class<?> type) {
        reloadOuterClassOf(type);
        reloadSuperClassOf(type);
        return addInterceptions(bytebuddy.redefine(type));
    }

    private Class<?> subclassClass(Class<?> type) {
        reload(type);
        return addInterceptions(bytebuddy.subclass(type));
    }

    private boolean isSystemClass(Class<?> type) {
        // Kijk of de classloader de bootstrap classloader is, of zo
        return type.getName().startsWith("java");
    }

    private void reloadOuterClassOf(Class<?> type) {
        Class<?> outer = type.getDeclaringClass();
        if (outer != null) {
            reloadOuterClassOf(outer);
            reload(outer);
        }
    }

    private void reloadSuperClassOf(Class<?> type) {
        Class<?> upper = type.getSuperclass();
        if (upper != null && upper != Object.class) {
            reloadSuperClassOf(upper);
            reload(upper);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> reload(Class<T> type) {
        if (cache.containsKey(type.getName())) {
            return (Class<T>) cache.get(type.getName());
        }
        if (isSystemClass(type)) {
            return type;
        }
        // Kan dit op een andere manier, bijvoorbeeld direct op de classloader?
        Class<T> reloaded = (Class<T>) new ByteBuddy()
            .redefine(type)
            .make()
            .load(bytebuddyCL, ClassLoadingStrategy.Default.INJECTION)
            .getLoaded();
        cache.put(type.getName(), reloaded);
        return reloaded;
    }

    private Class<?> addInterceptions(DynamicType.Builder<?> builder) {
        Class<?> type = builder
            .defineField(SYNTHETIC_FIELD_NAME, int.class, Modifier.PRIVATE)
            .method(ElementMatchers.named("equals"))
            .intercept(Advice.to(EqualsAdvice.class))
            .method(ElementMatchers.named("hashCode"))
            .intercept(Advice.to(HashCodeAdvice.class))
            .method(ElementMatchers.named("toString"))
            .intercept(Advice.to(ToStringAdvice.class))
            .make()
            .load(bytebuddyCL, ClassLoadingStrategy.Default.INJECTION)
            .getLoaded();
        cache.put(type.getName(), type);
        return type;
    }

    private static class EqualsAdvice {

        @Advice.OnMethodExit
        static void exit(
            @Advice.This Object me,
            @Advice.Argument(0) Object other,
            @Advice.FieldValue(SYNTHETIC_FIELD_NAME) int myHashCode,
            @Advice.Return(readOnly = false) boolean returnValue
        ) {
            if (other == null) {
                returnValue = false;
                return;
            }
            if (me == other) {
                returnValue = true;
                return;
            }
            if (me.getClass() != other.getClass()) {
                returnValue = false;
                return;
            }
            int otherHashCode;
            try {
                final Field hashCodeField = other.getClass().getDeclaredField(SYNTHETIC_FIELD_NAME);
                hashCodeField.setAccessible(true);
                otherHashCode = hashCodeField.getInt(other);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            returnValue = myHashCode == otherHashCode;
        }
    }

    private static class HashCodeAdvice {

        @Advice.OnMethodExit
        static void exit(
            @Advice.FieldValue(SYNTHETIC_FIELD_NAME) int hashCode,
            @Advice.Return(readOnly = false) int returnValue
        ) {
            returnValue = hashCode;
        }
    }

    private static class ToStringAdvice {

        @Advice.OnMethodExit
        static void exit(
            @Advice.This Object me,
            @Advice.Return(readOnly = false) String returnValue,
            @Advice.FieldValue(SYNTHETIC_FIELD_NAME) int hashCode
        ) {
            returnValue = "woei!" + me.getClass().getName() + "[" + hashCode + "]";
        }
    }

    // Custom ClassLoader that defines classes using ByteBuddy's ClassInjector
    private static class ByteBuddyClassLoader extends ClassLoader {

        public final Map<String, Class<?>> cache;

        public ByteBuddyClassLoader(ClassLoader parent, Map<String, Class<?>> cache) {
            super(parent);
            this.cache = cache;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (cache.containsKey(name)) {
                return cache.get(name);
            }
            ClassFileLocator locator = ClassFileLocator.ForClassLoader.of(getParent());
            try {
                ClassFileLocator.Resolution resolution = locator.locate(name);
                if (resolution.isResolved()) {
                    byte[] classBytes = resolution.resolve();
                    // Use the super.defineClass to bypass security checks
                    // ProtectionDomain could be customized for additional security
                    Class<?> type = super.defineClass(
                        name,
                        classBytes,
                        0,
                        classBytes.length,
                        this.getClass().getProtectionDomain()
                    );
                    cache.put(name, type);
                    return type;
                }
            } catch (Exception e) {
                // If something goes wrong, let the parent class loader attempt to load
            }
            return super.findClass(name);
        }
    }
}
