/*
 * Copyright 2010, 2012-2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Container and creator of prefabricated instances of objects and classes.
 *
 * @author Jan Ouwens
 */
public class PrefabValues {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_OBJECT_MAPPER = createPrimitiveObjectMapper();
    private final StaticFieldValueStash stash;
    private final Map<Class<?>, Tuple<?>> values = new HashMap<>();

    public PrefabValues() {
        this(new StaticFieldValueStash());
    }

    public PrefabValues(StaticFieldValueStash stash) {
        this.stash = stash;
    }

    /**
     * Backs up the values of all static member fields of the given type.
     *
     * @param type The type for which to store the values of static member
     *          fields.
     */
    public void backupToStash(Class<?> type) {
        stash.backup(type);
    }

    /**
     * Restores the values of all static member fields, for all types for which
     * they were stored at once.
     */
    public void restoreFromStash() {
        stash.restoreAll();
    }

    /**
     * Associates the specified values with the specified class in this
     * collection of prefabricated values.
     *
     * @param <T> The type of value to put into this {@link PrefabValues}.
     * @param type The class of the values.
     * @param red A value of type T.
     * @param black Another value of type T.
     */
    public <T> void put(Class<T> type, T red, T black) {
        values.put(type, new Tuple<T>(red, black));
    }

    /**
     * Copies all prefabricated values of the specified {@link PrefabValues} to
     * this one.
     *
     * @param from Prefabricated values to be copied to this
     *          {@link PrefabValues}.
     */
    public void putAll(PrefabValues from) {
        values.putAll(from.values);
    }

    /**
     * Tests whether prefabricated values exist for the specified class.
     *
     * @param type Class whose presence in this {@link PrefabValues} is to be
     *          tested.
     * @return True if prefabricated values exist for the specified class.
     */
    public boolean contains(Class<?> type) {
        return values.containsKey(type);
    }

    /**
     * Getter for the "red" prefabricated value of the specified type.
     *
     * @param type Class for which to return the prefabricated value.
     * @return The "red" prefabricated value for the specified type.
     */
    public <T> T getRed(Class<T> type) {
        return getTuple(type).red;
    }

    /**
     * Getter for the "black" prefabricated value of the specified type.
     *
     * @param type Class for which to return the prefabricated value.
     * @return The "black" prefabricated value for the specified type.
     */
    public <T> T getBlack(Class<T> type) {
        return getTuple(type).black;
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> getTuple(Class<T> type) {
        return (Tuple<T>)values.get(type);
    }

    /**
     * Returns a prefabricated value for type which is not equal to value.
     *
     * @param type Class for which to return a prefabricated value.
     * @param value An instance of type.
     * @return A prefabricated value for type which is not equal to value.
     */
    public Object getOther(Class<?> type, Object value) {
        if (type == null) {
            throw new ReflectionException("Type is null.");
        }

        if (value != null && !type.isAssignableFrom(value.getClass()) && !wraps(type, value.getClass())) {
            throw new ReflectionException("Type does not match value.");
        }

        Tuple<?> tuple = values.get(type);
        if (tuple == null) {
            throw new ReflectionException("No prefab values for " + type + " exist.");
        }

        if (type.isArray() && arraysAreDeeplyEqual(tuple.red, value)) {
            return tuple.black;
        }
        if (!type.isArray() && tuple.red.equals(value)) {
            return tuple.black;
        }

        return tuple.red;
    }

    private boolean wraps(Class<?> expectedClass, Class<?> actualClass) {
        return PRIMITIVE_OBJECT_MAPPER.get(expectedClass) == actualClass;
    }

    private boolean arraysAreDeeplyEqual(Object x, Object y) {
        // Arrays.deepEquals doesn't accept Object values so we need to wrap them in another array.
        return Arrays.deepEquals(new Object[] { x }, new Object[] { y });
    }

    /**
     * Creates instances for the specified type, and for the types of the
     * fields contained within the specified type, recursively, and adds them.
     *
     * Both created instances are guaranteed not to be equal to each other,
     * but are not guaranteed to be non-null. However, nulls will be very rare.
     *
     * @param type The type to create prefabValues for.
     * @throws RecursionException If recursion is detected.
     */
    public void putFor(Class<?> type) {
        putFor(type, new LinkedHashSet<Class<?>>());
    }

    private void putFor(Class<?> type, LinkedHashSet<Class<?>> typeStack) {
        if (noNeedToCreatePrefabValues(type)) {
            return;
        }
        if (typeStack.contains(type)) {
            throw new RecursionException(typeStack);
        }

        stash.backup(type);
        @SuppressWarnings("unchecked")
        LinkedHashSet<Class<?>> clone = (LinkedHashSet<Class<?>>)typeStack.clone();
        clone.add(type);

        if (type.isEnum()) {
            putEnumInstances(type);
        }
        else if (type.isArray()) {
            putArrayInstances(type, clone);
        }
        else {
            traverseFields(type, clone);
            createAndPutInstances(type);
        }
    }

    private boolean noNeedToCreatePrefabValues(Class<?> type) {
        return contains(type) || type.isPrimitive();
    }

    private <T> void putEnumInstances(Class<T> type) {
        T[] enumConstants = type.getEnumConstants();

        switch (enumConstants.length) {
            case 0:
                throw new ReflectionException("Enum " + type.getSimpleName() + " has no elements");
            case 1:
                put(type, enumConstants[0], enumConstants[0]);
                break;
            default:
                put(type, enumConstants[0], enumConstants[1]);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void putArrayInstances(Class<T> type, LinkedHashSet<Class<?>> typeStack) {
        Class<?> componentType = type.getComponentType();
        putFor(componentType, typeStack);
        T red = (T)Array.newInstance(componentType, 1);
        Array.set(red, 0, getRed(componentType));
        T black = (T)Array.newInstance(componentType, 1);
        Array.set(black, 0, getBlack(componentType));
        put(type, red, black);
    }

    private void traverseFields(Class<?> type, LinkedHashSet<Class<?>> typeStack) {
        for (Field field : FieldIterable.of(type)) {
            int modifiers = field.getModifiers();
            boolean isStaticAndFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
            if (!isStaticAndFinal) {
                putFor(field.getType(), typeStack);
            }
        }
    }

    private <T> void createAndPutInstances(Class<T> type) {
        nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues p =
                new nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues(new StaticFieldValueStash());
        for (Map.Entry<Class<?>, Tuple<?>> e : values.entrySet()) {
            p.addFactory((Class<Object>)e.getKey(), e.getValue().red, e.getValue().black);
        }

        ClassAccessor<T> accessor = ClassAccessor.of(type, p, false);
        T red = accessor.getRedObject();
        T black = accessor.getBlackObject();
        put(type, red, black);
    }

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
        return result;
    }

    private static final class Tuple<T> {
        private T red;
        private T black;

        private Tuple(T red, T black) {
            this.red = red;
            this.black = black;
        }
    }
}
