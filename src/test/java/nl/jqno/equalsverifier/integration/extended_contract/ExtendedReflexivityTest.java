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
package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Test;

import java.util.Objects;

public class ExtendedReflexivityTest extends IntegrationTestBase {
    @Test
    public void succeed_whenEqualsUsesEqualsMethodForObjects() {
        EqualsVerifier.forClass(UsesEqualsMethod.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsUsesDoubleEqualSignForObjects() {
        expectFailure("Reflexivity", "== used instead of .equals()", "stringField");
        EqualsVerifier.forClass(UsesDoubleEqualSign.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsUsesDoubleEqualSignForObject_givenDoubleEqualWarningIsSuppressed() {
        EqualsVerifier.forClass(UsesDoubleEqualSign.class)
                .suppress(Warning.REFERENCE_EQUALITY)
                .verify();
    }

    @Test
    public void succeed_whenEqualsUsesDoubleEqualSignForObject_givenObjectDoesntDeclareEquals() {
        EqualsVerifier.forClass(FieldHasNoEquals.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsUsesDoubleEqualSignForObject_givenObjectIsAnInterface() {
        EqualsVerifier.forClass(FieldIsInterface.class)
                .verify();
    }

    static final class UsesEqualsMethod {
        private final String s;

        public UsesEqualsMethod(String s) { this.s = s; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesEqualsMethod)) {
                return false;
            }
            UsesEqualsMethod other = (UsesEqualsMethod)obj;
            return Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class UsesDoubleEqualSign {
        private final String stringField;

        public UsesDoubleEqualSign(String s) { this.stringField = s; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsesDoubleEqualSign)) {
                return false;
            }
            UsesDoubleEqualSign other = (UsesDoubleEqualSign)obj;
            return stringField == other.stringField;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class FieldHasNoEquals {
        @SuppressWarnings("unused")
        private final NoEquals field;

        public FieldHasNoEquals(NoEquals field) { this.field = field; }
        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }

        static final class NoEquals {}
    }

    static final class FieldIsInterface {
        @SuppressWarnings("unused")
        private final Interface field;

        public FieldIsInterface(Interface field) { this.field = field; }
        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }

        interface Interface {}
    }
}
