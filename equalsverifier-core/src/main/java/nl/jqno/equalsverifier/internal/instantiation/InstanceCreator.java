package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

class InstanceCreator<T> {

    private final Class<T> type;
    private final ClassProbe<T> probe;

    public InstanceCreator(ClassProbe<T> probe) {
        this.type = probe.getType();
        this.probe = probe;
    }

    public T instantiate(Map<Field, Object> values) {
        return probe.isRecord() ? createRecordInstance(values) : createClassInstance(values);
    }

    private T createRecordInstance(Map<Field, Object> values) {
        List<Object> params = new ArrayList<>();
        for (Field f : fields()) {
            Object value = values.get(f);
            if (value == null) {
                Object def = PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(f.getType());
                params.add(def);
            } else {
                params.add(value);
            }
        }
        RecordProbe<T> recordProbe = new RecordProbe<>(type);
        return recordProbe.callRecordConstructor(params);
    }

    private T createClassInstance(Map<Field, Object> values) {
        T instance = Instantiator.of(type).instantiate();
        ObjectAccessor<T> accessor = ObjectAccessor.of(instance);
        for (Field f : fields()) {
            Object value = values.get(f);
            if (value == null) {
                accessor.withDefaultedField(f);
            } else {
                accessor.withFieldSetTo(f, value);
            }
        }
        return instance;
    }

    private FieldIterable fields() {
        return FieldIterable.ofIgnoringStatic(type);
    }
}
