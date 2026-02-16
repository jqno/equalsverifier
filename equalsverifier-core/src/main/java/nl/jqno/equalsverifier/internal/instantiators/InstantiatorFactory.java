package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.exceptions.InstantiatorException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.util.Formatter;
import org.objenesis.Objenesis;

public final class InstantiatorFactory {

    private InstantiatorFactory() {
        // Do not instantiate
    }

    /**
     * Factory method for obtaining an appropriate Instantiator for the class given via the ClassProbe parameter.
     *
     * Note that the given class is the class that will be instantiated. No subclass will be substituted if the given
     * class is abstract or an interface.
     *
     * @param <T>                  Represents the type of the class to instantiate.
     * @param probe                Represents the class to instantiate.
     * @param objenesis            To instantiate non-record classes.
     * @param forceFinalMeansFinal Force "final means final" (JEP 500) mode.
     * @return an {@code InstanceCreator} for the given class.
     */
    public static <T> Instantiator<T> of(ClassProbe<T> probe, Objenesis objenesis, boolean forceFinalMeansFinal) {
        return of(probe, null, objenesis, forceFinalMeansFinal);
    }

    /**
     * Factory method for obtaining an appropriate Instantiator for the class given via the ClassProbe parameter.
     *
     * Note that the given class is the class that will be instantiated. No subclass will be substituted if the given
     * class is abstract or an interface.
     *
     * @param <T>                  Represents the type of the class to instantiate.
     * @param probe                Represents the class to instantiate. Can be null if a factory is provided.
     * @param factory              A factory that can create instances of {@code T}. Can be null if a probe is provided.
     * @param objenesis            To instantiate non-record classes.
     * @param forceFinalMeansFinal Force "final means final" (JEP 500) mode.
     * @return an {@code InstanceCreator} for the given class.
     */
    public static <T> Instantiator<T> of(
            ClassProbe<T> probe,
            InstanceFactory<T> factory,
            Objenesis objenesis,
            boolean forceFinalMeansFinal) {
        if (factory != null) {
            return new ProvidedFactoryInstantiator<>(factory);
        }

        if (probe == null) {
            throw new EqualsVerifierInternalBugException("No InstanceFactory given; must provide a type!");
        }

        Class<T> type = probe.getType();
        if (probe.isRecord()) {
            return new RecordConstructorInstantiator<>(type);
        }

        boolean finalMeansFinal = forceFinalMeansFinal || cantReflectivelyModifyFinal();
        if (!finalMeansFinal || !hasFinalFields(type)) {
            return new ReflectionInstantiator<>(probe, objenesis);
        }

        var constructor = findConstructorMatchingFields(probe);
        if (constructor != null && !probe.isAbstract()) {
            return new ClassConstructorInstantiator<>(type, constructor);
        }

        throw failure(probe);
    }

    private static boolean cantReflectivelyModifyFinal() {
        @SuppressWarnings("unused")
        class C {
            final Object o = new Object();
        }

        try {
            var f = C.class.getDeclaredField("o");
            f.setAccessible(true);
            f.set(new C(), new Object());
            return false;
        }
        catch (IllegalAccessException | NoSuchFieldException ignored) {
            return true;
        }
    }

    private static <T> boolean hasFinalFields(Class<T> type) {
        for (var f : FieldIterable.ofIgnoringStatic(type)) {
            if (f.isFinal()) {
                return true;
            }
        }
        return false;
    }

    private static <T> Constructor<T> findConstructorMatchingFields(ClassProbe<T> probe) {
        Class<?> type = probe.getType();
        var fieldTypes = new ArrayList<Class<?>>();
        for (var f : FieldIterable.ofIgnoringStatic(type)) {
            fieldTypes.add(f.getType());
        }
        var fields = fieldTypes.toArray(new Class<?>[] {});
        var constructor = probe.findConstructor(fields);

        if (constructor.isPresent()) {
            try {
                var c = constructor.get();
                // Calling setAccessible(true) might trigger InaccessibleObjectException when JPMS is active.
                // In that case, we can't (reflectively) call the constructor, so the constructor isn't usable and we shouldn't return it here.
                c.setAccessible(true);
                return c;
            }
            catch (InaccessibleObjectException e) {
                return null;
            }
        }
        return null;
    }

    private static RuntimeException failure(ClassProbe<?> probe) {
        var msg = """
                  Cannot instantiate %%.
                     Use #withFactory() so EqualsVerifier can construct %% instances without using reflection.""";
        var typeName = probe.getType().getSimpleName();
        return new InstantiatorException(Formatter.of(msg, typeName, typeName).format());
    }
}
