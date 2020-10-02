package nl.jqno.equalsverifier.internal.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveMappers {

    public static final Map<Class<?>, Class<?>> PRIMITIVE_OBJECT_MAPPER =
            createPrimitiveObjectMapper();
    public static final Map<Class<?>, Object> DEFAULT_VALUE_MAPPER =
            createDefaultValueMapper(false);
    public static final Map<Class<?>, Object> DEFAULT_WRAPPED_VALUE_MAPPER =
            createDefaultValueMapper(true);

    private static Map<Class<?>, Class<?>> createPrimitiveObjectMapper() {
        Map<Class<?>, Class<?>> result = new HashMap<>();
        result.put(boolean.class, Boolean.class);
        result.put(byte.class, Byte.class);
        result.put(char.class, Character.class);
        result.put(double.class, Double.class);
        result.put(float.class, Float.class);
        result.put(int.class, Integer.class);
        result.put(long.class, Long.class);
        result.put(short.class, Short.class);
        return Collections.unmodifiableMap(result);
    }

    private static Map<Class<?>, Object> createDefaultValueMapper(boolean includeWrapped) {
        Map<Class<?>, Object> result = new HashMap<>();
        result.put(boolean.class, false);
        result.put(byte.class, Byte.valueOf((byte) 0));
        result.put(char.class, Character.valueOf((char) 0));
        result.put(double.class, Double.valueOf(0));
        result.put(float.class, Float.valueOf(0));
        result.put(int.class, Integer.valueOf(0));
        result.put(long.class, Long.valueOf(0));
        result.put(short.class, Short.valueOf((short) 0));
        if (includeWrapped) {
            result.put(Boolean.class, false);
            result.put(Byte.class, Byte.valueOf((byte) 0));
            result.put(Character.class, Character.valueOf((char) 0));
            result.put(Double.class, Double.valueOf(0));
            result.put(Float.class, Float.valueOf(0));
            result.put(Integer.class, Integer.valueOf(0));
            result.put(Long.class, Long.valueOf(0));
            result.put(Short.class, Short.valueOf((short) 0));
        }
        return Collections.unmodifiableMap(result);
    }
}
