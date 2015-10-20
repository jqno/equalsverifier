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

import nl.jqno.equalsverifier.internal.ClassAccessor;
import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;

import java.util.*;

public final class Configuration<T> {
    private final Class<T> type;
    private final PrefabValues prefabValues;

    private final Set<String> ignoredFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final boolean hasRedefinedSuperclass;
    private final Class<? extends T> redefinedSubclass;
    private final boolean usingGetClass;
    private final EnumSet<Warning> warningsToSuppress;

    // CHECKSTYLE: ignore ParameterNumber for 1 line.
    private Configuration(Class<T> type, PrefabValues prefabValues, Set<String> ignoredFields,
                          CachedHashCodeInitializer<T> cachedHashCodeInitializer, boolean hasRedefinedSuperclass,
                          Class<? extends T> redefinedSubclass, boolean usingGetClass, EnumSet<Warning> warningsToSuppress) {

        this.type = type;
        this.prefabValues = prefabValues;
        this.ignoredFields = ignoredFields;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
        this.hasRedefinedSuperclass = hasRedefinedSuperclass;
        this.redefinedSubclass = redefinedSubclass;
        this.usingGetClass = usingGetClass;
        this.warningsToSuppress = warningsToSuppress;
    }

    public static <T> Configuration<T> of(Class<T> type) {
        return new Configuration<>(type, new PrefabValues(new StaticFieldValueStash()), new HashSet<String>(),
                CachedHashCodeInitializer.<T>passthrough(), false, null, false, EnumSet.noneOf(Warning.class));
    }

    public Class<T> getType() {
        return type;
    }

    public PrefabValues getPrefabValues() {
        return prefabValues;
    }

    public Configuration<T> withIgnoredFields(String[] value) {
        return new Configuration<>(type, prefabValues, new HashSet<>(Arrays.asList(value)),
                cachedHashCodeInitializer, hasRedefinedSuperclass, redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public Set<String> getIgnoredFields() {
        return Collections.unmodifiableSet(ignoredFields);
    }

    public Configuration<T> withCachedHashCodeInitializer(CachedHashCodeInitializer<T> value) {
        return new Configuration<>(type, prefabValues, ignoredFields, value,
                hasRedefinedSuperclass, redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public CachedHashCodeInitializer<T> getCachedHashCodeInitializer() {
        return cachedHashCodeInitializer;
    }

    public Configuration<T> withRedefinedSuperclass() {
        return new Configuration<>(type, prefabValues, ignoredFields,
                cachedHashCodeInitializer, true, redefinedSubclass, usingGetClass, warningsToSuppress);
    }

    public boolean hasRedefinedSuperclass() {
        return hasRedefinedSuperclass;
    }

    public Configuration<T> withRedefinedSubclass(Class<? extends T> value) {
        return new Configuration<>(type, prefabValues, ignoredFields,
                cachedHashCodeInitializer, hasRedefinedSuperclass, value, usingGetClass, warningsToSuppress);
    }

    public Class<? extends T> getRedefinedSubclass() {
        return redefinedSubclass;
    }

    public Configuration<T> withUsingGetClass() {
        return new Configuration<>(type, prefabValues, ignoredFields,
                cachedHashCodeInitializer, hasRedefinedSuperclass, redefinedSubclass, true, warningsToSuppress);
    }

    public boolean isUsingGetClass() {
        return usingGetClass;
    }

    public Configuration<T> withWarningsToSuppress(EnumSet<Warning> value) {
        return new Configuration<>(type, prefabValues, ignoredFields,
                cachedHashCodeInitializer, hasRedefinedSuperclass, redefinedSubclass, usingGetClass, value);
    }

    public EnumSet<Warning> getWarningsToSuppress() {
        return EnumSet.copyOf(warningsToSuppress);
    }

    public ClassAccessor<T> createClassAccessor() {
        return ClassAccessor.of(type, prefabValues, warningsToSuppress.contains(Warning.ANNOTATION));
    }
}
