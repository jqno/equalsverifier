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
package nl.jqno.equalsverifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.jqno.equalsverifier.internal.FieldIterable;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/**
 * Records an initializer for a cached hash code (field name and recompute
 * method), if any, for the object to be verified.
 * 
 * EqualsVerifier may then, instead of calling <code>Object.hashCode()</code> to
 * obtain the hash code, call the {@link #getInitializedHashCode(Object)} method
 * in this class:
 * 
 * * If this class has recorded a cached hash code initializer for the object,
 * that method will recompute and update the cached hash code in the object
 * automatically, before returning the result of <code>Object.hashCode()</code>.
 * 
 * * If this class has not recorded a cached hash code initializer for the
 * object, it will simply return the value of <code>Object.hashCode()</code> as
 * normal instead.
 *
 * @author Niall Gallagher, Jan Ouwens
 */
class CachedHashCodeInitializer<T> {
    private final boolean passthrough;
    private final Field cachedHashCodeField;
    private final Method calculateMethod;
    private final T example;

    private CachedHashCodeInitializer() {
        this.passthrough = true;
        this.cachedHashCodeField = null;
        this.calculateMethod = null;
        this.example = null;
    }

    public CachedHashCodeInitializer(Class<?> type, String cachedHashCodeField, String calculateHashCodeMethod, T example) {
        this.passthrough = false;
        this.cachedHashCodeField = findCachedHashCodeField(type, cachedHashCodeField);
        this.calculateMethod = findCalculateHashCodeMethod(type, calculateHashCodeMethod);
        this.example = example;
    }

    public static <T> CachedHashCodeInitializer<T> passthrough() {
        return new CachedHashCodeInitializer<>();
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
        try {
            cachedHashCodeField.set(object, 0); // zero the field first, in case calculateMethod checks it
            Integer recomputedHashCode = (Integer) calculateMethod.invoke(object);
            cachedHashCodeField.set(object, recomputedHashCode);
        }
        catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    private Field findCachedHashCodeField(Class<?> type, String cachedHashCodeFieldName) {
        for (Field candidateField : FieldIterable.of(type)) {
            if (candidateField.getName().equals(cachedHashCodeFieldName)) {
                if (!Modifier.isPublic(candidateField.getModifiers()) && candidateField.getType().equals(int.class)) {
                    candidateField.setAccessible(true);
                    return candidateField;
                }
            }
        }
        throw new IllegalArgumentException("Cached hashCode: Could not find cachedHashCodeField: must be 'private int " + cachedHashCodeFieldName + ";'");
    }

    private Method findCalculateHashCodeMethod(Class<?> type, String calculateHashCodeMethodName) {
        Class<?> currentClass = type;
        while (!currentClass.equals(Object.class)) {
            try {
                Method method = currentClass.getDeclaredMethod(calculateHashCodeMethodName);
                if (!Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(int.class)) {
                    method.setAccessible(true);
                    return method;
                }
            }
            catch (NoSuchMethodException ignore) {}
            currentClass = currentClass.getSuperclass();
        }
        throw new IllegalArgumentException("Cached hashCode: Could not find calculateHashCodeMethod: must be 'private int " + calculateHashCodeMethodName + "()'");
    }
}
