package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.reflection.SuperclassIterable;

/**
 * Records an initializer for a cached hash code (field name and recompute method), if any, for the object to be
 * verified.
 *
 * <p>
 * EqualsVerifier may then, instead of calling <code>Object.hashCode()</code> to obtain the hash code, call the
 * {@link #getInitializedHashCode(Object)} method in this class:
 *
 * <p>
 * * If this class has recorded a cached hash code initializer for the object, that method will recompute and update the
 * cached hash code in the object automatically, before returning the result of <code>Object.hashCode()</code>.
 *
 * <p>
 * * If this class has not recorded a cached hash code initializer for the object, it will simply return the value of
 * <code>Object.hashCode()</code> as normal instead.
 */
public class CachedHashCodeInitializer<T> {

    private final boolean passthrough;
    private final FieldProbe cachedHashCodeField;
    private final Method calculateMethod;
    private final T example;

    private CachedHashCodeInitializer() {
        this(true, null, null, null);
    }

    private CachedHashCodeInitializer(
            boolean passthrough,
            FieldProbe cachedHashCodeField,
            Method calculateMethod,
            T example) {
        this.passthrough = passthrough;
        this.cachedHashCodeField = cachedHashCodeField;
        this.calculateMethod = calculateMethod;
        this.example = example;
    }

    public CachedHashCodeInitializer(
            Class<?> type,
            String cachedHashCodeField,
            String calculateHashCodeMethod,
            T example) {
        this.passthrough = false;
        this.cachedHashCodeField = findCachedHashCodeField(type, cachedHashCodeField);
        this.calculateMethod = findCalculateHashCodeMethod(type, calculateHashCodeMethod, false);
        this.example = example;
    }

    public static <T> CachedHashCodeInitializer<T> passthrough() {
        return new CachedHashCodeInitializer<>();
    }

    public static <T> CachedHashCodeInitializer<T> lombokCachedHashcode(T example) {
        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) example.getClass();
        return new CachedHashCodeInitializer<>(false,
                findCachedHashCodeField(type, "$hashCodeCache"),
                findCalculateHashCodeMethod(type, "hashCode", true),
                example);
    }

    public boolean isPassthrough() {
        return passthrough;
    }

    public T getExample() {
        return example;
    }

    public String getCachedHashCodeFieldName() {
        if (isPassthrough()) {
            return null;
        }
        return cachedHashCodeField.getName();
    }

    public int getInitializedHashCode(Object object) {
        if (!passthrough) {
            recomputeCachedHashCode(object);
        }
        return object.hashCode();
    }

    private void recomputeCachedHashCode(Object object) {
        rethrow(() -> {
            Field f = cachedHashCodeField.getField();
            f.setAccessible(true);
            // zero the field first, in case calculateMethod checks it
            f.set(object, 0);
            Integer recomputedHashCode = (Integer) calculateMethod.invoke(object);
            f.set(object, recomputedHashCode);
        });
    }

    private static FieldProbe findCachedHashCodeField(Class<?> type, String cachedHashCodeFieldName) {
        for (FieldProbe candidate : FieldIterable.of(type)) {
            if (candidate.getName().equals(cachedHashCodeFieldName)) {
                if (!candidate.isPublic() && candidate.getType().equals(int.class)) {
                    return candidate;
                }
            }
        }
        throw new IllegalArgumentException("Cached hashCode: Could not find cachedHashCodeField: must be 'private int "
                + cachedHashCodeFieldName + ";'");
    }

    private static Method findCalculateHashCodeMethod(
            Class<?> type,
            String calculateHashCodeMethodName,
            boolean acceptPublicMethod) {
        for (Class<?> currentClass : SuperclassIterable.ofIncludeSelf(type)) {
            try {
                Method method = currentClass.getDeclaredMethod(calculateHashCodeMethodName);
                if ((acceptPublicMethod || !Modifier.isPublic(method.getModifiers()))
                        && method.getReturnType().equals(int.class)) {
                    method.setAccessible(true);
                    return method;
                }
            }
            catch (NoSuchMethodException ignore) {
                // Method not found; continue.
            }
        }
        throw new IllegalArgumentException(
                "Cached hashCode: Could not find calculateHashCodeMethod: must be 'private int "
                        + calculateHashCodeMethodName + "()'");
    }
}
