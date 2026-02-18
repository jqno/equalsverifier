package nl.jqno.equalsverifier;

import java.util.Set;

/**
 * Simple container for primitive and object values used to define a provided instantiator.
 */
public interface Values {

    /**
     * Returns the field names that were requested so far.
     *
     * @return The field names thar were requested so far.
     */
    Set<String> getRequestedFields();

    /**
     * Returns the {@code boolean} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code boolean} value
     */
    boolean getBoolean(String fieldName);

    /**
     * Returns the {@code byte} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code byte} value
     */
    byte getByte(String fieldName);

    /**
     * Returns the {@code char} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code char} value
     */
    char getChar(String fieldName);

    /**
     * Returns the {@code double} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code double} value
     */
    double getDouble(String fieldName);

    /**
     * Returns the {@code float} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code float} value
     */
    float getFloat(String fieldName);

    /**
     * Returns the {@code int} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code int} value
     */
    int getInt(String fieldName);

    /**
     * Returns the {@code long} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code long} value
     */
    long getLong(String fieldName);

    /**
     * Returns the {@code short} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code short} value
     */
    short getShort(String fieldName);

    /**
     * Returns the {@code String} value stored under the given field name.
     *
     * @param fieldName the name of the field
     * @return the stored {@code String} value
     */
    String getString(String fieldName);

    /**
     * Returns the value stored under the given field name, cast to the desired type.
     *
     * @param <T>       the type to cast to
     * @param fieldName the name of the field
     * @return the stored value cast to the requested type
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    <T> T get(String fieldName);
}
