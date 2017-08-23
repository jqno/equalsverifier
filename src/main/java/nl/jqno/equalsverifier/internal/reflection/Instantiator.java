/*
 * Copyright 2010,2012,2015,2017 Jan Ouwens
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
package nl.jqno.equalsverifier.internal.reflection;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;

/**
 * Instantiates objects of a given class.
 *
 * @author Jan Ouwens
 *
 * @param <T> {@link Instantiator} instantiates objects of this class, or of an
 *          anonymous subclass of this class.
 */
public final class Instantiator<T> {
    private static final List<String> FORBIDDEN_PACKAGES =
            Arrays.asList("java.", "javax.", "sun.", "com.sun.", "org.w3c.dom.");

    private final Class<T> type;
    private Objenesis objenesis;

    /**
     * Private constructor. Call {@link #of(Class)} to instantiate.
     */
    private Instantiator(Class<T> type) {
        this.type = type;
        this.objenesis = new ObjenesisStd();
    }

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
            return new Instantiator<>(giveDynamicSubclass(type));
        }
        return new Instantiator<>(type);
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
        Class<T> proxyClass = giveDynamicSubclass(type);
        return objenesis.newInstance(proxyClass);
    }

    @SuppressWarnings("unchecked")
    private static synchronized <S> Class<S> giveDynamicSubclass(Class<S> superclass) {
        boolean isSystemClass = isSystemClass(superclass.getName());
        String namePrefix = isSystemClass ? "$" : "";
        String name = namePrefix + superclass.getName() + "$$DynamicSubclass";

        Class<S> existsAlready = (Class<S>)classForName(name);
        if (existsAlready != null) {
            return existsAlready;
        }

        Class<?> context = isSystemClass ? Instantiator.class : superclass;
        return (Class<S>)new ByteBuddy()
                .with(TypeValidation.DISABLED)
                .subclass(superclass)
                .name(name)
                .make()
                .load(context.getClassLoader(), ClassLoadingStrategy.Default.INJECTION.with(context.getProtectionDomain()))
                .getLoaded();
    }

    private static boolean isSystemClass(String className) {
        for (String prefix : FORBIDDEN_PACKAGES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
