package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import org.objenesis.Objenesis;

/**
 * Creates an instance of a class or record.
 */
public class InstanceCreator<T> {

    private final Class<T> type;
    private final ClassProbe<T> probe;
    private final Instantiator<T> instantiator;

    /**
     * Constructor.
     *
     * @param probe     Represents the class to instantiate.
     * @param objenesis To instantiate non-record classes.
     */
    public InstanceCreator(ClassProbe<T> probe, Objenesis objenesis) {
        this.type = probe.getType();
        this.probe = probe;
        this.instantiator = Instantiator.of(type, objenesis);
    }

    /**
     * Creates an instance of the given type, with its field set to the given values. If no value is given for a
     * specific field, the field will be set to its default value: null for object references, 0 for numbers, false for
     * booleans.
     *
     * @param values Values to assign to the instance's fields.
     * @return An instance with assigned values.
     */
    public T instantiate(Map<Field, Object> values) {
        return probe.isRecord() ? createRecordInstance(values) : createClassInstance(values);
    }

    /**
     * Creates a new instance with all fields set to the same value as their counterparts from {@code original}.
     *
     * @param original The instance to copy.
     * @return A copy of the given original.
     */
    public T copy(Object original) {
        var values = new HashMap<Field, Object>();
        for (FieldProbe p : fields(original.getClass())) {
            Object value = p.getValue(original);
            values.put(p.getField(), value);
        }
        return instantiate(values);
    }

    private T createRecordInstance(Map<Field, Object> values) {
        var params = new ArrayList<Object>();
        traverseFields(values, (p, v) -> params.add(v));
        var recordProbe = new RecordProbe<T>(type);
        return recordProbe.callRecordConstructor(params);
    }

    private T createClassInstance(Map<Field, Object> values) {
        T instance = instantiator.instantiate();
        traverseFields(values, (p, v) -> new FieldMutator(p).setNewValue(instance, v));
        return instance;
    }

    private void traverseFields(Map<Field, Object> values, BiConsumer<FieldProbe, Object> setValue) {
        for (FieldProbe p : fields(type)) {
            Object value = values.get(p.getField());
            if (value == null) {
                value = PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(p.getType());
            }
            setValue.accept(p, value);
        }
    }

    private FieldIterable fields(Class<?> typeWithFields) {
        return FieldIterable.ofIgnoringStatic(typeWithFields);
    }
}
