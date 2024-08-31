package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.util.Configuration;

public class ModernSubjectCreator<T> implements SubjectCreator<T> {

    private final TypeTag typeTag;
    private final InstanceCreator instanceCreator;

    public ModernSubjectCreator(TypeTag typeTag, InstanceCreator instanceCreator) {
        this.typeTag = typeTag;
        this.instanceCreator = instanceCreator;
    }

    @Override
    public T plain() {
        return createInstance(empty());
    }

    @Override
    public T withFieldDefaulted(Field field) {
        return createInstance(with(field, null));
    }

    @Override
    public T withAllFieldsDefaulted(Configuration<T> config) {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            values.put(f, null);
        }
        return createInstance(values);
    }

    @Override
    public T withAllFieldsDefaultedExcept(Field field) {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            if (!f.equals(field)) {
                values.put(f, null);
            }
        }
        return createInstance(values);
    }

    @Override
    public T withFieldSetTo(Field field, Object value) {
        return createInstance(with(field, value));
    }

    @Override
    public T withFieldChanged(Field field) {
        Object value = instantiate(field).getBlue();
        return createInstance(with(field, value));
    }

    @Override
    public T withAllFieldsChanged() {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            Object value = instantiate(f).getBlue();
            values.put(f, value);
        }
        return createInstance(values);
    }

    private T createInstance(Map<Field, Object> values) {
        for (Field f : fields()) {
            if (!values.containsKey(f)) {
                Object value = instantiate(f).getRed();
                values.put(f, value);
            }
        }
        // maak een instance met veld-waardes uit `values`: kijk af uit FallbackFactory
        return null;
    }

    private Map<Field, Object> empty() {
        return new HashMap<>();
    }

    private Map<Field, Object> with(Field f, Object v) {
        Map<Field, Object> result = empty();
        result.put(f, v);
        return result;
    }

    private FieldIterable fields() {
        return FieldIterable.of(typeTag.getType());
    }

    private Tuple<?> instantiate(Field f) {
        return instanceCreator.instantiate(TypeTag.of(f, typeTag));
    }
}
