package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import nl.jqno.equalsverifier.InstanceFactory;
import nl.jqno.equalsverifier.internal.util.Context;

/**
 * Creates an instance of a class using a factory provided by the user.
 */
public final class ProvidedFactoryInstantiator<T> implements Instantiator<T> {

    private final InstanceFactory<? extends T> instanceFactory;
    private Set<String> lastRequestedFields = null;

    /**
     * Package private constructor. Use {@link InstantiatorFactory#of(Context)} instead.
     *
     * @param instanceFactory The factory to use.
     */
    ProvidedFactoryInstantiator(InstanceFactory<? extends T> instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    /** {@inheritDoc} */
    @Override
    public T instantiate(Map<Field, Object> values) {
        var v = ConcreteValues.of(values);
        var result = instanceFactory.create(v);
        lastRequestedFields = v.getRequestedFields();
        return result;
    }

    public Set<String> getLastRequestedFields() {
        return Collections.unmodifiableSet(lastRequestedFields);
    }
}
