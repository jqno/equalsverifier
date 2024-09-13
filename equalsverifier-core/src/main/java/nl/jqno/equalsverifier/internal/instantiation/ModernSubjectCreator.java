package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.*;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

public class ModernSubjectCreator<T> implements SubjectCreator<T> {

    private final TypeTag typeTag;
    private final Class<T> type;
    private final Configuration<T> config;
    private final InstanceCreator instanceCreator;
    private final ClassProbe<T> classProbe;

    public ModernSubjectCreator(
        TypeTag typeTag,
        Configuration<T> config,
        InstanceCreator instanceCreator
    ) {
        this.typeTag = typeTag;
        this.type = typeTag.getType();
        this.config = config;
        this.instanceCreator = instanceCreator;
        this.classProbe = new ClassProbe<>(type);
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
    public T withAllFieldsDefaulted() {
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
        if (FieldProbe.of(field, config).isStatic()) {
            return plain();
        }
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

    @Override
    public T withAllFieldsShallowlyChanged() {
        Map<Field, Object> values = empty();
        for (Field f : nonSuperFields()) {
            Object value = instantiate(f).getBlue();
            values.put(f, value);
        }
        return createInstance(values);
    }

    @Override
    public T copy(T original) {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            Object value = FieldProbe.of(f, config).getValue(original);
            values.put(f, value);
        }
        return createInstance(values);
    }

    @Override
    public Object copyIntoSuperclass(T original) {
        TypeTag superTag = new TypeTag(type.getSuperclass());
        ModernSubjectCreator<T> superCreator = new ModernSubjectCreator<T>(
            superTag,
            config,
            instanceCreator
        );

        Map<Field, Object> values = empty();
        for (Field f : superCreator.fields()) {
            Object value = FieldProbe.of(f, config).getValue(original);
            values.put(f, value);
        }

        return superCreator.createInstance(values);
    }

    private T createInstance(Map<Field, Object> givens) {
        Map<Field, Object> values = determineValues(givens);
        return classProbe.isRecord() ? createRecordInstance(values) : createClassInstance(values);
    }

    private Map<Field, Object> determineValues(Map<Field, Object> givens) {
        Map<Field, Object> values = new HashMap<>(givens);
        for (Field f : fields()) {
            boolean fieldIsAbsent = !values.containsKey(f);
            boolean fieldCannotBeNull =
                values.get(f) == null && !FieldProbe.of(f, config).canBeDefault();
            if (fieldIsAbsent || fieldCannotBeNull) {
                Object value = instantiate(f).getRed();
                values.put(f, value);
            }
        }
        return values;
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

    private Map<Field, Object> empty() {
        return new HashMap<>();
    }

    private Map<Field, Object> with(Field f, Object v) {
        Map<Field, Object> result = empty();
        result.put(f, v);
        return result;
    }

    private FieldIterable fields() {
        return FieldIterable.ofIgnoringStatic(type);
    }

    private FieldIterable nonSuperFields() {
        return FieldIterable.ofIgnoringSuperAndStatic(type);
    }

    private Tuple<?> instantiate(Field f) {
        return instanceCreator.instantiate(TypeTag.of(f, typeTag));
    }
}
