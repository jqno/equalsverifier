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
    private final Class<T> type;
    private final Configuration<T> config;
    private final ValueProvider valueProvider;
    private final ClassProbe<T> classProbe;

    public ModernSubjectCreator(
        TypeTag typeTag,
        Configuration<T> config,
        ValueProvider valueProvider
    ) {
        this.typeTag = typeTag;
        this.type = typeTag.getType();
        this.config = config;
        this.valueProvider = valueProvider;
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
        if (FieldProbe.of(field).isStatic()) {
            return plain();
        }
        Object value = valuesFor(field).getBlue();
        return createInstance(with(field, value));
    }

    @Override
    public T withAllFieldsChanged() {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            Object value = valuesFor(f).getBlue();
            values.put(f, value);
        }
        return createInstance(values);
    }

    @Override
    public T withAllFieldsShallowlyChanged() {
        Map<Field, Object> values = empty();
        for (Field f : nonSuperFields()) {
            Object value = valuesFor(f).getBlue();
            values.put(f, value);
        }
        return createInstance(values);
    }

    @Override
    public T copy(T original) {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            Object value = FieldProbe.of(f).getValue(original);
            values.put(f, value);
        }
        return createInstance(values);
    }

    @Override
    public Object copyIntoSuperclass(T original) {
        Map<Field, Object> values = empty();
        for (Field f : superFields()) {
            Object value = FieldProbe.of(f).getValue(original);
            values.put(f, value);
        }

        InstanceCreator<? super T> superCreator = new InstanceCreator<>(
            new ClassProbe<>(type.getSuperclass())
        );
        return superCreator.instantiate(values);
    }

    @Override
    public <S extends T> S copyIntoSubclass(T original, Class<S> subType) {
        Map<Field, Object> values = empty();
        for (Field f : fields()) {
            Object value = FieldProbe.of(f).getValue(original);
            values.put(f, value);
        }

        InstanceCreator<S> subCreator = new InstanceCreator<>(new ClassProbe<>(subType));
        return subCreator.instantiate(values);
    }

    private T createInstance(Map<Field, Object> givens) {
        Map<Field, Object> values = determineValues(givens);
        InstanceCreator<T> instaceCreator = new InstanceCreator<>(classProbe);
        return instaceCreator.instantiate(values);
    }

    private Map<Field, Object> determineValues(Map<Field, Object> givens) {
        Map<Field, Object> values = new HashMap<>(givens);
        for (Field f : fields()) {
            boolean fieldIsAbsent = !values.containsKey(f);
            boolean fieldCannotBeNull =
                values.get(f) == null && !FieldProbe.of(f).canBeDefault(config);
            if (fieldIsAbsent || fieldCannotBeNull) {
                Object value = valuesFor(f).getRed();
                values.put(f, value);
            }
        }
        return values;
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

    private FieldIterable superFields() {
        return FieldIterable.ofIgnoringStatic(type.getSuperclass());
    }

    private FieldIterable nonSuperFields() {
        return FieldIterable.ofIgnoringSuperAndStatic(type);
    }

    private Tuple<?> valuesFor(Field f) {
        return valueProvider.provide(TypeTag.of(f, typeTag));
    }
}
