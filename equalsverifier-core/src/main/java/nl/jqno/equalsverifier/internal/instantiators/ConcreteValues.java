package nl.jqno.equalsverifier.internal.instantiators;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.Values;

/**
 * Simple container for primitive and object values used to define a provided instantiator. This implementation of the
 * {@link Values} interface is mutable so a collection of values can be constructed easily.
 */
public final class ConcreteValues implements Values {
    private final Map<String, Object> values = java.util.Collections.synchronizedMap(new HashMap<>());

    /**
     * Stores a value under the given field name. This method is package‑protected and does not perform null checks.
     *
     * @param fieldName the name of the field
     * @param value     the value to store
     */
    void put(String fieldName, Object value) {
        values.put(fieldName, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String fieldName) {
        return (Boolean) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(String fieldName) {
        return (Byte) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char getChar(String fieldName) {
        return (Character) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(String fieldName) {
        return (Double) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String fieldName) {
        return (Float) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String fieldName) {
        return (Integer) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String fieldName) {
        return (Long) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(String fieldName) {
        return (Short) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String fieldName) {
        return (String) values.get(fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "TypeParameterUnusedInFormals" })
    public <T> T get(String fieldName) {
        return (T) values.get(fieldName);
    }
}
