package nl.jqno.equalsverifier.internal.reflection.instantiation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Creates an instance of a class or record.
 */
class InstanceCreator<T> {

    private final Class<T> type;
    private final ClassProbe<T> probe;

    /** Constructor. */
    public InstanceCreator(ClassProbe<T> probe) {
        this.type = probe.getType();
        this.probe = probe;
    }

    /**
     * Creates an instance of the given type, with its field set to the given values. If no value
     * is given for a specific field, the field will be set to its default value: null for object
     * references, 0 for numbers, false for booleans.
     */
    public T instantiate(Map<Field, Object> values) {
        return probe.isRecord() ? createRecordInstance(values) : createClassInstance(values);
    }

    private T createRecordInstance(Map<Field, Object> values) {
        List<Object> params = new ArrayList<>();
        traverseFields(values, (f, v) -> params.add(v));
        RecordProbe<T> recordProbe = new RecordProbe<>(type);
        return recordProbe.callRecordConstructor(params);
    }

    private T createClassInstance(Map<Field, Object> values) {
        T instance = Instantiator.of(type).instantiate();
        traverseFields(
            values,
            (f, v) -> new FieldMutator(FieldProbe.of(f)).setNewValue(instance, v)
        );
        return instance;
    }

    private void traverseFields(Map<Field, Object> values, BiConsumer<Field, Object> setValue) {
        for (Field f : fields()) {
            Object value = values.get(f);
            if (value == null) {
                value = PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(f.getType());
            }
            setValue.accept(f, value);
        }
    }

    private FieldIterable fields() {
        return FieldIterable.ofIgnoringStatic(type);
    }
}
