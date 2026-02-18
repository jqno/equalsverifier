package nl.jqno.equalsverifier.internal.instantiators;

import java.lang.reflect.Field;
import java.util.*;

import nl.jqno.equalsverifier.Values;
import nl.jqno.equalsverifier.internal.util.PrimitiveMappers;

/**
 * Simple container for primitive and object values used to define a provided instantiator. This implementation of the
 * {@link Values} interface is mutable so a collection of values can be constructed easily.
 */
public final class ConcreteValues implements Values {
    private final Map<String, Object> values = new HashMap<>();
    private final Set<String> requestedFields = new HashSet<>();

    @Override
    public Set<String> getRequestedFields() {
        return Collections.unmodifiableSet(requestedFields);
    }

    /**
     * Creates an instance of {@link ConcreteValues} from a Map.
     *
     * @param values The values to add to the new instance.
     * @return An instance of {@link ConcreteValues} with the given values.
     */
    public static ConcreteValues of(Map<Field, Object> values) {
        var result = new ConcreteValues();
        for (var f : values.keySet()) {
            result.values.put(f.getName(), values.get(f));
        }
        return result;
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
        requestedFields.add(fieldName);
        return (T) values.get(fieldName);
    }

    @SuppressWarnings("unchecked")
    private <T> T safe(Class<T> type, String fieldName) {
        requestedFields.add(fieldName);
        var value = values.get(fieldName);
        return value != null ? (T) value : (T) PrimitiveMappers.DEFAULT_VALUE_MAPPER.get(type);
    }

    @Override
    public String toString() {
        return "ConcreteValues:[" + values + "]";
    }
}
