package nl.jqno.equalsverifier.internal.reflection;

import org.objenesis.Objenesis;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Instantiates objects of a given class.
 *
 * @param <T> {@link Instantiator} instantiates objects of this class, or of an anonymous subclass of this class.
 */
public final class Instantiator<T> {

    private final Class<T> type;
    private final ObjectInstantiator<T> objenesisInstantiator;

    /** Private constructor. Call {@link #of(Class, Objenesis)} to instantiate. */
    private Instantiator(Class<T> type, Objenesis objenesis) {
        this.type = type;
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
        ClassProbe<T> probe = ClassProbe.of(type);
        Class<T> concrete = SubtypeManager.findInstantiableSubclass(probe).get();
        return new Instantiator<>(concrete, objenesis);
    }

    /**
     * The actual type that will be instantiated. Could be a subclass of the requested type.
     *
     * @return The actual type that will be instantiated.
     */
    public Class<T> getType() {
        return type;
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
}
