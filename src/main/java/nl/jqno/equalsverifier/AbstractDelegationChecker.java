/*
 * Copyright 2010-2011, 2013, 2015 Jan Ouwens
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.*;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.lang.reflect.Field;

import static nl.jqno.equalsverifier.internal.Assert.fail;

class AbstractDelegationChecker<T> implements Checker {
    private final Class<T> type;
    private final PrefabValues prefabValues;
    private final ClassAccessor<T> classAccessor;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public AbstractDelegationChecker(Configuration<T> config) {
        this.type = config.getType();
        this.prefabValues = config.getPrefabValues();
        this.classAccessor = config.createClassAccessor();
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
    }

    @Override
    public void check() {
        checkAbstractEqualsAndHashCode();

        checkAbstractDelegationInFields();

        T instance = this.getRedPrefabValue(type);
        if (instance == null) {
            instance = classAccessor.getRedObject();
        }
        T copy = this.getBlackPrefabValue(type);
        if (copy == null) {
            copy = classAccessor.getBlackObject();
        }
        checkAbstractDelegation(instance, copy);

        checkAbstractDelegationInSuper();
    }

    private void checkAbstractEqualsAndHashCode() {
        boolean equalsIsAbstract = classAccessor.isEqualsAbstract();
        boolean hashCodeIsAbstract = classAccessor.isHashCodeAbstract();

        if (equalsIsAbstract && hashCodeIsAbstract) {
            fail(Formatter.of("Abstract delegation: %%'s equals and hashCode methods are both abstract. They should be concrete.",
                    type.getSimpleName()));
        }
        else if (equalsIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(type, true, true));
        }
        else if (hashCodeIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(type, false, true));
        }
    }

    private void checkAbstractDelegationInFields() {
        for (Field field : FieldIterable.of(type)) {
            Class<?> c = field.getType();
            Object instance = safelyGetInstance(c);
            if (instance != null) {
                Object copy = safelyCopyInstance(instance);
                checkAbstractMethods(c, instance, copy, true);
            }
        }
    }

    private void checkAbstractDelegation(T instance, T copy) {
        checkAbstractMethods(type, instance, copy, false);
    }

    private void checkAbstractDelegationInSuper() {
        Class<? super T> superclass = type.getSuperclass();
        ClassAccessor<? super T> superAccessor = classAccessor.getSuperAccessor();

        boolean equalsIsAbstract = superAccessor.isEqualsAbstract();
        boolean hashCodeIsAbstract = superAccessor.isHashCodeAbstract();
        if (equalsIsAbstract != hashCodeIsAbstract) {
            fail(buildSingleAbstractMethodErrorMessage(superclass, equalsIsAbstract, false));
        }
        if (equalsIsAbstract && hashCodeIsAbstract) {
            return;
        }

        Object instance = getRedPrefabValue(superclass);
        if (instance == null) {
            instance = superAccessor.getRedObject();
        }
        Object copy = getBlackPrefabValue(type);
        if (copy == null) {
            copy = superAccessor.getBlackObject();
        }
        checkAbstractMethods(superclass, instance, copy, false);
    }

    private Formatter buildSingleAbstractMethodErrorMessage(Class<?> c, boolean isEqualsAbstract, boolean bothShouldBeConcrete) {
        return Formatter.of("Abstract delegation: %%'s %% method is abstract, but %% is not.\n%%",
                c.getSimpleName(),
                isEqualsAbstract ? "equals" : "hashCode",
                isEqualsAbstract ? "hashCode" : "equals",
                bothShouldBeConcrete ? "Both should be concrete." : "Both should be either abstract or concrete.");
    }

    @SuppressWarnings("unchecked")
    private <S> S getRedPrefabValue(Class<?> c) {
        return (S)prefabValues.giveRed(TypeTag.make(c));
    }

    @SuppressWarnings("unchecked")
    private <S> S getBlackPrefabValue(Class<?> c) {
        return (S)prefabValues.giveBlack(TypeTag.make(c));
    }

    private Object safelyGetInstance(Class<?> c) {
        Object result = getRedPrefabValue(c);
        if (result != null) {
            return result;
        }
        try {
            return Instantiator.of(c).instantiate();
        }
        catch (Exception ignored) {
            // If it fails for some reason, any reason, just return null.
            return null;
        }
    }

    private Object safelyCopyInstance(Object o) {
        try {
            return ObjectAccessor.of(o).copy();
        }
        catch (Throwable ignored) {
            return o;
        }
    }

    @SuppressFBWarnings(value = "DE_MIGHT_IGNORE", justification = "These exceptions will re-occur and be handled later.")
    private <S> void checkAbstractMethods(Class<?> instanceClass, S instance, S copy, boolean prefabPossible) {
        try {
            instance.equals(copy);
        }
        catch (AbstractMethodError e) {
            fail(buildAbstractDelegationErrorMessage(instanceClass, prefabPossible, "equals", e.getMessage()), e);
        }
        catch (Exception ignored) {
            // Skip. We only care about AbstractMethodError at this point;
            // other errors will be handled later.
        }

        try {
            cachedHashCodeInitializer.getInitializedHashCode(instance);
        }
        catch (AbstractMethodError e) {
            fail(buildAbstractDelegationErrorMessage(instanceClass, prefabPossible, "hashCode", e.getMessage()), e);
        }
        catch (Exception ignored) {
            // Skip. We only care about AbstractMethodError at this point;
            // other errors will be handled later.
        }
    }

    private Formatter buildAbstractDelegationErrorMessage(Class<?> c, boolean prefabPossible, String method, String originalMessage) {
        Formatter prefabFormatter = Formatter.of("\nAdd prefab values for %%.", c.getName());

        return Formatter.of("Abstract delegation: %%'s %% method delegates to an abstract method:\n %%%%",
                c.getSimpleName(), method, originalMessage, prefabPossible ? prefabFormatter.format() : "");
    }
}
