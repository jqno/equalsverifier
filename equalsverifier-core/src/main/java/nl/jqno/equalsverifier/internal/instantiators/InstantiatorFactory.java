package nl.jqno.equalsverifier.internal.instantiators;

import java.util.ArrayList;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
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
     * @param <T>       Represents the type of the class to instantiate.
     * @param probe     Represents the class to instantiate.
     * @param objenesis To instantiate non-record classes.
     * @return an {@code InstanceCreator} for the given class.
     */
    public static <T> Instantiator<T> of(ClassProbe<T> probe, Objenesis objenesis) {
        return of(probe, null, objenesis);
    }

    /**
     * Factory method for obtaining an appropriate Instantiator for the class given via the ClassProbe parameter.
     *
     * Note that the given class is the class that will be instantiated. No subclass will be substituted if the given
     * class is abstract or an interface.
     *
     * @param <T>       Represents the type of the class to instantiate.
     * @param probe     Represents the class to instantiate.
     * @param factory   A factory that can create instances of {@code T}, or null.
     * @param objenesis To instantiate non-record classes.
     * @return an {@code InstanceCreator} for the given class.
     */
    public static <T> Instantiator<T> of(ClassProbe<T> probe, InstanceFactory<T> factory, Objenesis objenesis) {
        if (factory != null) {
            return new ProvidedFactoryInstantiator<>(factory);
        }

        Class<T> type = probe.getType();
        if (probe.isRecord() || constructorMatchesFields(type)) {
            return new ConstructorInstantiator<>(probe);
        }

        return new ReflectionInstantiator<>(probe, objenesis);
    }

    private static boolean constructorMatchesFields(Class<?> type) {
        var fieldTypes = new ArrayList<Class<?>>();
        for (var f : FieldIterable.ofIgnoringStatic(type)) {
            fieldTypes.add(f.getType());
        }
        var fields = fieldTypes.toArray(new Class<?>[] {});

        try {
            type.getDeclaredConstructor(fields);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }
}
