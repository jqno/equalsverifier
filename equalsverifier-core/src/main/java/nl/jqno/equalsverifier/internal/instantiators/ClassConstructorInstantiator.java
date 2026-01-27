package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import org.objenesis.Objenesis;

/**
 * Creates an instance of a class by calling the constructor.
 */
public class ClassConstructorInstantiator<T> implements Instantiator<T> {
    private final Class<T> type;
    private final Constructor<T> constructor;

    /**
     * Package private constructor. Use {@link InstantiatorFactory#of(ClassProbe, Objenesis)} instead.
     *
     * @param type The type to instantiate.
     */
    ClassConstructorInstantiator(Class<T> type, Constructor<T> constructor) {
        this.type = type;
        this.constructor = constructor;
    }

    /** {@inheritDoc} */
    @Override
    public T instantiate(Map<Field, Object> values) {
        var params = new ArrayList<Object>();
        for (var f : fields()) {
            Object value = values.get(f);
            if (value == null) {
                value = PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(f.getType());
            }
            params.add(value);
        }
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(params.toArray());
        }
        catch (ReflectiveOperationException e) {
            throw new ReflectionException(e);
        }
    }

    private List<Field> fields() {
        var result = new ArrayList<Field>();
        for (var f : FieldIterable.ofIgnoringStatic(type)) {
            result.add(f.getField());
        }
        return result;
    }
}
