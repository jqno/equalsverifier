/*
 * Copyright 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import nl.jqno.equalsverifier.EqualsVerifier;

import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class EnumTest {
    @Test
    public void succeed_whenClassIsAnEnum() {
        EqualsVerifier.forClass(Enum.class)
                .verify();
    }

    @Test
    public void ignoreSingleValueEnum() {
        EqualsVerifier.forClass(SingletonContainer.class)
                .verify();
    }

    @Test
    public void useSingleValueEnum() {
        EqualsVerifier.forClass(SingletonUser.class)
                .verify();
    }

    @Test
    public void useSingleValueEnumWithoutNullCheck() {
        EqualsVerifier.forClass(NullThrowingSingletonUser.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    enum Enum {
        ONE, TWO, THREE
    }

    enum Singleton { INSTANCE }

    static final class SingletonContainer {
        private final int i;

        @SuppressWarnings("unused")
        private final Singleton singleton = Singleton.INSTANCE;

        public SingletonContainer(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SingletonContainer)) {
                return false;
            }
            SingletonContainer other = (SingletonContainer)obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class SingletonUser {
        private final Singleton singleton;

        public SingletonUser(Singleton singleton) {
            this.singleton = singleton;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class NullThrowingSingletonUser {
        private final Singleton singleton;

        public NullThrowingSingletonUser(Singleton singleton) {
            this.singleton = singleton;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            return singleton.hashCode();
        }
    }
}
