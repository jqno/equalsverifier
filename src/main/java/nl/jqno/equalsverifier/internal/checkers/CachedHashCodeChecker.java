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
package nl.jqno.equalsverifier.internal.checkers;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.Formatter;

import java.util.EnumSet;

import static nl.jqno.equalsverifier.internal.Assert.*;

public class CachedHashCodeChecker<T> implements Checker {
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final EnumSet<Warning> warningsToSuppress;

    public CachedHashCodeChecker(Configuration<T> config) {
        this.cachedHashCodeInitializer = config.getCachedHashCodeInitializer();
        this.warningsToSuppress = config.getWarningsToSuppress();
    }

    @Override
    public void check() {
        if (cachedHashCodeInitializer.isPassthrough()) {
            return;
        }
        if (warningsToSuppress.contains(Warning.NONFINAL_FIELDS)) {
            fail(Formatter.of("Cached hashCode: EqualsVerifier can only check cached hashCodes for immutable classes."));
        }

        T reference = cachedHashCodeInitializer.getExample();
        if (warningsToSuppress.contains(Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE)) {
            if (reference != null) {
                fail(Formatter.of("Cached hashCode: example must be null if %% is suppressed", Warning.NO_EXAMPLE_FOR_CACHED_HASHCODE.name()));
            }
        }
        else {
            if (reference == null) {
                fail(Formatter.of("Cached hashCode: example cannot be null."));
            }
            int actualHashCode = reference.hashCode();
            int recomputedHashCode = cachedHashCodeInitializer.getInitializedHashCode(reference);

            assertEquals(Formatter.of("Cached hashCode: hashCode is not properly initialized."), actualHashCode, recomputedHashCode);
            assertFalse(Formatter.of("Cached hashCode: example.hashCode() cannot be zero. Please choose a different example."),
                    actualHashCode == 0);
        }
    }
}
