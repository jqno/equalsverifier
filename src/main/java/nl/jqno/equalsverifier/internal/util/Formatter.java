package nl.jqno.equalsverifier.internal.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;

/**
 * Formats a string with the contents of one or more objects.
 *
 * <p>If possible, uses each object's {@code toString} method. If this throws an exception,
 * Formatter creates its own string representation of the object, containing its class name and the
 * contents of its fields.
 */
public final class Formatter {
    private final String message;
    private Object[] objects;

    /** Private constructor. Call {@link #of(String, Object...)} to instantiate. */
    private Formatter(String message, Object... objects) {
        if (message == null) {
            throw new NullPointerException();
        }
        this.message = message;
        this.objects = objects;
    }

    /**
     * Factory method.
     *
     * @param message The string that will be formatted. The substring %% represents the location
     *     where each object's will string representation will be inserted.
     * @param objects The objects whose string representation will be inserted into the message
     *     string.
     * @return A {@code Formatter}.
     */
    public static Formatter of(String message, Object... objects) {
        return new Formatter(message, objects);
    }

    /**
     * Formats the message with the given objects.
     *
     * @return The message, with the given objects's string representations inserted into it.
     * @throws IllegalStateException if the number of %%'s in the message does not match the number
     *     of objects.
     */
    public String format() {
        String result = message;
        for (Object object : objects) {
            String s = result.replaceFirst("%%", Matcher.quoteReplacement(stringify(object)));
            if (result.equals(s)) {
                throw new IllegalStateException("Too many parameters");
            }
            result = s;
        }
        if (result.contains("%%")) {
            throw new IllegalStateException("Not enough parameters");
        }
        return result;
    }

    private String stringify(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return obj.toString();
        } catch (Throwable e) {
            return stringifyByReflection(obj)
                    + "-throws "
                    + e.getClass().getSimpleName()
                    + "("
                    + e.getMessage()
                    + ")";
        }
    }

    private String stringifyByReflection(Object obj) {
        StringBuilder result = new StringBuilder();

        Class<?> type = obj.getClass();
        ObjectAccessor<?> accessor = ObjectAccessor.of(obj);

        result.append("[");
        String typeName = type.getSimpleName().replaceAll("\\$\\$DynamicSubclass.*", "");
        result.append(typeName);

        for (Field field : FieldIterable.of(type)) {
            String fieldName = field.getName();
            result.append(" ");
            result.append(fieldName);
            result.append("=");
            Object value = accessor.getField(field);
            result.append(stringify(value));
        }

        result.append("]");
        return result.toString();
    }
}
