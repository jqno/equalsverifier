package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.objenesis.Objenesis;

/**
 * Creates an instance of a class using a factory provided by the user.
 */
public final class ProvidedFactoryInstantiator<T> implements Instantiator<T> {

    private final InstanceFactory<? extends T> instanceFactory;
    private final boolean throwing;

    /**
     * Package private constructor. Use {@link InstantiatorFactory#of(ClassProbe, Objenesis, boolean)} instead.
     *
     * @param instanceFactory The factory to use.
     * @param throwing        Whether or not to throw an exception when the factory asks for an unknown field.
     */
    ProvidedFactoryInstantiator(InstanceFactory<? extends T> instanceFactory, boolean throwing) {
        this.instanceFactory = instanceFactory;
        this.throwing = throwing;
    }

    /** {@inheritDoc} */
    @Override
    public T instantiate(Map<Field, Object> values) {
        var v = ConcreteValues.of(values, throwing);
        return instanceFactory.create(v);
    }
}
