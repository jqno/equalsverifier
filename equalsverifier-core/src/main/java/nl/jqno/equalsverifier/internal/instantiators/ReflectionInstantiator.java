package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;
import org.objenesis.Objenesis;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Creates an instance of a class using reflection on potentially final fields.
 */
public final class ReflectionInstantiator<T> implements Instantiator<T> {

    private final Class<T> type;
    private final ClassProbe<T> probe;
    private final ObjectInstantiator<T> objenesisInstantiator;

    /**
     * Package private constructor. Use {@link Instantiator#of(ClassProbe, Objenesis)} instead.
     *
     * @param probe     The ClassProbe for the type.
     * @param objenesis The Objenesis instance to use.
     */
    ReflectionInstantiator(ClassProbe<T> probe, Objenesis objenesis) {
        this.type = probe.getType();
        this.probe = ClassProbe.of(type);
        this.objenesisInstantiator = objenesis.getInstantiatorOf(type);
    }

    /** {@inheritDoc}} */
    @Override
    public T instantiate(Map<Field, Object> values) {
        return probe.isRecord() ? createRecordInstance(values) : createClassInstance(values);
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
        T instance = objenesisInstantiator.newInstance();
        traverseFields(values, (p, v) -> new FieldMutator(p).setNewValue(instance, v));
        return instance;
    }

    private void traverseFields(Map<Field, Object> values, BiConsumer<FieldProbe, Object> setValue) {
        for (FieldProbe p : FieldIterable.ofIgnoringStatic(type)) {
            Object value = values.get(p.getField());
            if (value == null) {
                value = PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(p.getType());
            }
            setValue.accept(p, value);
        }
    }
}
