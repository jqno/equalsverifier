/*
 * Copyright 2010, 2014 Jan Ouwens
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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class SignatureTest extends IntegrationTestBase {
    private static final String OVERLOADED = "Overloaded";
    private static final String SIGNATURE_SHOULD_BE = "Signature should be";
    private static final String SIGNATURE = "public boolean equals(Object obj)";

    @Test
    public void fail_whenEqualsIsOverloadedWithTypeInsteadOfObject() {
        expectOverloadFailure("Parameter should be an Object, not " + OverloadedWithOwnType.class.getSimpleName());
        EqualsVerifier.forClass(OverloadedWithOwnType.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsOverloadedWithTwoParameters() {
        expectOverloadFailure("Too many parameters");
        EqualsVerifier.forClass(OverloadedWithTwoParameters.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsOverloadedWithNoParameter() {
        expectOverloadFailure("No parameter");
        EqualsVerifier.forClass(OverloadedWithNoParameter.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsOverloadedWithUnrelatedParameter() {
        expectOverloadFailure("Parameter should be an Object");
        EqualsVerifier.forClass(OverloadedWithUnrelatedParameter.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsProperlyOverriddenButAlsoOverloaded() {
        expectOverloadFailure("More than one equals method found");
        EqualsVerifier.forClass(OverloadedAndOverridden.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .verify();
    }

    @Test
    public void succeed_whenEqualsIsNeitherOverriddenOrOverloaded_givenInheritedDirectlyWarningIsSuppressed() {
        EqualsVerifier.forClass(NoEqualsMethod.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    private void expectOverloadFailure(String extraMessage) {
        expectFailure(OVERLOADED, SIGNATURE_SHOULD_BE, SIGNATURE, extraMessage);
    }

    static final class OverloadedWithOwnType {
        private final int i;

        OverloadedWithOwnType(int i) { this.i = i; }

        public boolean equals(OverloadedWithOwnType obj) {
            if (obj == null) {
                return false;
            }
            return i == obj.i;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class OverloadedWithTwoParameters {
        @SuppressWarnings("unused")
        private final int i;

        OverloadedWithTwoParameters(int i) { this.i = i; }

        public boolean equals(Object red, Object black) {
            return red == null ? black == null : red.equals(black);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class OverloadedWithNoParameter {
        @SuppressWarnings("unused")
        private final int i;

        OverloadedWithNoParameter(int i) { this.i = i; }

        public boolean equals() {
            return false;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class OverloadedWithUnrelatedParameter {
        private final int i;

        OverloadedWithUnrelatedParameter(int i) { this.i = i; }

        public boolean equals(int i) {
            return this.i == i;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class OverloadedAndOverridden {
        private final int i;

        OverloadedAndOverridden(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OverloadedAndOverridden)) {
                return false;
            }
            return i == ((OverloadedAndOverridden)obj).i;
        }

        public boolean equals(OverloadedAndOverridden obj) {
            if (obj == null) {
                return false;
            }
            return i == obj.i;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class NoEqualsMethod {
        @SuppressWarnings("unused")
        private final int i;

        public NoEqualsMethod(int i) { this.i = i; }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
