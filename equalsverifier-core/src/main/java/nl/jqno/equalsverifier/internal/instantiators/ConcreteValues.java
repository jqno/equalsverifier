package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.Values;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Simple container for primitive and object values used to define a provided instantiator. This implementation of the
 * {@link Values} interface is mutable so a collection of values can be constructed easily.
 */
public final class ConcreteValues implements Values {
    private final Map<String, Object> values = new HashMap<>();
    private final boolean throwing;

    /**
     * Creates an instance of {@link ConcreteValues} from a Map.
     *
     * @param values   The values to add to the new instance.
     * @param throwing Whether or not to throw when an unknown value is requested.
     * @return An instance of {@link ConcreteValues} with the given values.
     */
    public static ConcreteValues of(Map<Field, Object> values, boolean throwing) {
        var result = new ConcreteValues(throwing);
        for (var f : values.keySet()) {
            result.values.put(f.getName(), values.get(f));
        }
        return result;
    }

    private ConcreteValues(boolean throwing) {
        this.throwing = throwing;
    }

    /**
     * Stores a value under the given field name.
     *
     * @param fieldName the name of the field
     * @param value     the value to store
     */
    public void put(String fieldName, Object value) {
        values.put(fieldName, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String fieldName) {
        return safe(boolean.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(String fieldName) {
        return safe(byte.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(String fieldName) {
        return safe(char.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(String fieldName) {
        return safe(double.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String fieldName) {
        return safe(float.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String fieldName) {
        return safe(int.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String fieldName) {
        return safe(long.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(String fieldName) {
        return safe(short.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String fieldName) {
        return safe(String.class, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "TypeParameterUnusedInFormals" })
    public <T> T get(String fieldName) {
        if (!values.containsKey(fieldName) && throwing) {
            throw new ReflectionException(Formatter.of("Attempted to get non-existing field %%.", fieldName).format());
        }
        return (T) values.get(fieldName);
    }

    @SuppressWarnings("unchecked")
    private <T> T safe(Class<T> type, String fieldName) {
        var value = get(fieldName);
        return value != null ? (T) value : (T) PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(type);
    }
}
