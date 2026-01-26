package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.RecordProbe;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import org.objenesis.Objenesis;

/**
 * Creates an instance of a class by calling the constructor.
 */
public class ConstructorInstantiator<T> implements Instantiator<T> {
    private final ClassProbe<T> probe;
    private final Class<T> type;

    /**
     * Package private constructor. Use {@link InstantiatorFactory#of(ClassProbe, Objenesis)} instead.
     *
     * @param type The type to instantiate.
     */
    ConstructorInstantiator(ClassProbe<T> probe) {
        this.probe = probe;
        this.type = probe.getType();
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
        var recordProbe = new RecordProbe<T>(type);
        return recordProbe.callRecordConstructor(params);
    }

    private List<Field> fields() {
        return probe.isRecord() ? recordFields() : classFields();
    }

    private List<Field> recordFields() {
        var result = new ArrayList<Field>();
        for (var component : type.getRecordComponents()) {
            try {
                result.add(type.getDeclaredField(component.getName()));
            }
            catch (NoSuchFieldException e) {
                throw new ReflectionException(e);
            }
        }
        return result;
    }

    private List<Field> classFields() {
        var result = new ArrayList<Field>();
        for (var f : FieldIterable.ofIgnoringStatic(type)) {
            result.add(f.getField());
        }
        return result;
    }
}
