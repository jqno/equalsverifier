package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
        traverseFields(values, (f, v) -> params.add(v));
        RecordProbe<T> recordProbe = new RecordProbe<>(type);
        return recordProbe.callRecordConstructor(params);
    }

    private T createClassInstance(Map<Field, Object> values) {
        T instance = Instantiator.of(type).instantiate();
        ObjectAccessor<T> accessor = ObjectAccessor.of(instance);
        traverseFields(values, (f, v) -> accessor.withFieldSetTo(f, v));
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
