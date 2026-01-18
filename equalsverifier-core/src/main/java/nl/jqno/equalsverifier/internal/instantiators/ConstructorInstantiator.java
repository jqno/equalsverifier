package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.RecordProbe;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import org.objenesis.Objenesis;

/**
 * Creates an instance of a class by calling the constructor.
 */
public class ConstructorInstantiator<T> implements Instantiator<T> {
    private final Class<T> type;

    /**
     * Package private constructor. Use {@link InstantiatorFactory#of(ClassProbe, Objenesis)} instead.
     *
     * @param type The type to instantiate.
     */
    ConstructorInstantiator(Class<T> type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public T instantiate(Map<Field, Object> values) {
        var params = new ArrayList<Object>();
        for (var component : type.getRecordComponents()) {
            try {
                Field f = type.getDeclaredField(component.getName());
                Object value = values.get(f);
                if (value == null) {
                    value = PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(f.getType());
                }
                params.add(value);
            }
            catch (NoSuchFieldException e) {
                throw new ReflectionException(e);
            }
        }
        var recordProbe = new RecordProbe<T>(type);
        return recordProbe.callRecordConstructor(params);
    }

}
