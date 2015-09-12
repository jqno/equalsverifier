/*
 * Copyright 2010,2012,2015 Jan Ouwens
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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Modifier;

/**
 * Instantiates objects of a given class.
 *
 * @author Jan Ouwens
 *
 * @param <T> {@link Instantiator} instantiates objects of this class, or of an
 *          anonymous subclass of this class.
 */
public final class Instantiator<T> {
    private final Class<T> type;
    private Objenesis objenesis;

    /**
     * Factory method.
     *
     * @param <T> The class on which {@link Instantiator} operates.
     * @param type The class on which {@link Instantiator} operates. Should be
     *          the same as T.
     * @return An {@link Instantiator} for {@link #type}.
     */
    public static <T> Instantiator<T> of(Class<T> type) {
        if (Modifier.isAbstract(type.getModifiers())) {
            return new Instantiator<>(createDynamicSubclass(type));
        }
        return new Instantiator<>(type);
    }

    /**
     * Private constructor. Call {@link #of(Class)} to instantiate.
     */
    private Instantiator(Class<T> type) {
        this.type = type;
        this.objenesis = new ObjenesisStd();
    }

    /**
     * Instantiates an object of type T.
     *
     * All fields will be initialized to their initial values.
     * I.e., 0 for ints, null for objects, etc.
     *
     * @return An object of type T.
     */
    public T instantiate() {
        return objenesis.newInstance(type);
    }

    /**
     * Instantiates an anonymous subclass of T. The subclass is
     * generated dynamically.
     *
     * @return An instance of an anonymous subclass of T.
     */
    public T instantiateAnonymousSubclass() {
        Class<T> proxyClass = createDynamicSubclass(type);
        return objenesis.newInstance(proxyClass);
    }

    @SuppressWarnings("unchecked")
    private static <S> Class<S> createDynamicSubclass(Class<S> superclass) {
        DynamicType.Builder<S> builder = createBuilder(superclass);
        return (Class<S>) builder
                .name(new NamingStrategy.Fixed(superclass.getName() + "$$DynamicSubclass"))
                .make()
                .load(superclass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
    }

    @SuppressWarnings("unchecked")
    private static <S> DynamicType.Builder<S> createBuilder(Class<S> superclass) {
        ByteBuddy byteBuddy = new ByteBuddy();
        if (superclass.isInterface()) {
            return (DynamicType.Builder<S>) byteBuddy.subclass(Object.class).implement(superclass);
        }
        else {
            return byteBuddy.subclass(superclass);
        }
    }
}
