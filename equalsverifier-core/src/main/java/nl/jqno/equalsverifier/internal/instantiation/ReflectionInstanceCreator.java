package nl.jqno.equalsverifier.internal.instantiation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import org.objenesis.Objenesis;

/**
 * Creates an instance of a class using reflection on potentially final fields.
 */
public final class ReflectionInstanceCreator<T> implements InstanceCreator<T> {

    private final Class<T> type;
    private final ClassProbe<T> probe;
    private final Instantiator<T> instantiator;

    /**
     * Private constructor. Use {@link InstanceCreator#of(ClassProbe, Objenesis)} instead.
     *
     * @param probe     The ClassProbe for the type.
     * @param objenesis The Objenesis instance to use.
     */
    ReflectionInstanceCreator(ClassProbe<T> probe, Objenesis objenesis) {
        this.type = probe.getType();
        this.instantiator = Instantiator.of(this.type, objenesis);
        this.probe = ClassProbe.of(this.type);
    }

    /** {@inheritDoc}} */
    @Override
    public Class<T> getActualType() {
        return type;
    }

    /** {@inheritDoc}} */
    @Override
    public T instantiate(Map<Field, Object> values) {
        return probe.isRecord() ? createRecordInstance(values) : createClassInstance(values);
    }

    /** {@inheritDoc}} */
    @Override
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

    private T createClassInstance(Map<Field, Object> values) {
        if (probe.isAbstract()) {
            throw new ReflectionException("Cannot instantiate abstract class " + probe.getType().getName());
        }
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
