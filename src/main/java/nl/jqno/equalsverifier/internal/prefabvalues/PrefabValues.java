/*
 * Copyright 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal.prefabvalues;

import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.exceptions.TypeTagRecursionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class PrefabValues {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_OBJECT_MAPPER = createPrimitiveObjectMapper();

    private final Cache cache = new Cache();
    private final FactoryCache factoryCache = new FactoryCache();
    private final FallbackFactory fallbackFactory = new FallbackFactory();
    private final StaticFieldValueStash stash;

    public PrefabValues(StaticFieldValueStash stash) {
        this.stash = stash;
    }

    public void backupToStash(Class<?> type) {
        stash.backup(type);
    }

    public void restoreFromStash() {
        stash.restoreAll();
    }

    public <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        factoryCache.put(type, factory);
    }

    public <T> void addFactory(Class<T> type, T red, T black) {
        factoryCache.put(type, new SimpleFactory<>(red, black));
    }

    public <T> T giveRed(TypeTag tag) {
        return this.<T>giveTuple(tag, emptyStack()).getRed();
    }

    public <T> T giveBlack(TypeTag tag) {
        return this.<T>giveTuple(tag, emptyStack()).getBlack();
    }

    public <T> T giveOther(TypeTag tag, T value) {
        Class<T> type = tag.getType();
        if (value != null && !type.isAssignableFrom(value.getClass()) && !wraps(type, value.getClass())) {
            throw new ReflectionException("TypeTag does not match value.");
        }

        Tuple<T> tuple = giveTuple(tag, emptyStack());
        if (type.isArray() && arraysAreDeeplyEqual(tuple.getRed(), value)) {
            return tuple.getBlack();
        }
        if (!type.isArray() && tuple.getRed().equals(value)) {
            return tuple.getBlack();
        }
        return tuple.getRed();
    }

    private boolean wraps(Class<?> expectedClass, Class<?> actualClass) {
        return PRIMITIVE_OBJECT_MAPPER.get(expectedClass) == actualClass;
    }

    private boolean arraysAreDeeplyEqual(Object x, Object y) {
        // Arrays.deepEquals doesn't accept Object values so we need to wrap them in another array.
        return Arrays.deepEquals(new Object[] { x }, new Object[] { y });
    }

    private <T> Tuple<T> giveTuple(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        realizeCacheFor(tag, typeStack);
        return cache.getTuple(tag);
    }

    private LinkedHashSet<TypeTag> emptyStack() {
        return new LinkedHashSet<>();
    }

    /* default */ <T> void realizeCacheFor(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        if (!cache.contains(tag)) {
            Tuple<T> tuple = createTuple(tag, typeStack);
            addToCache(tag, tuple);
        }
    }

    private <T> Tuple<T> createTuple(TypeTag tag, LinkedHashSet<TypeTag> typeStack) {
        if (typeStack.contains(tag)) {
            throw new TypeTagRecursionException(typeStack);
        }

        Class<T> type = tag.getType();
        if (factoryCache.contains(type)) {
            PrefabValueFactory<T> factory = factoryCache.get(type);
            return factory.createValues(tag, this);
        }
        stash.backup(type);
        return fallbackFactory.createValues(tag, this, typeStack);
    }

    private void addToCache(TypeTag tag, Tuple<?> tuple) {
        cache.put(tag, tuple.getRed(), tuple.getBlack());
    }

    // When this method is removed, make Cache.cache private
    // CHECKSTYLE: ignore MethodName for 1 line.
    nl.jqno.equalsverifier.internal.PrefabValues $toOld() {
        nl.jqno.equalsverifier.internal.PrefabValues result = new nl.jqno.equalsverifier.internal.PrefabValues();
        for (Map.Entry<TypeTag, Tuple> e : cache.cache.entrySet()) {
            result.put(e.getKey().getType(), e.getValue().getRed(), e.getValue().getBlack());
        }
        return result;
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
}
