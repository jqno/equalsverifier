/*
 * Copyright 2009-2011, 2014 Jan Ouwens
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
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class FloatAndDoubleTest extends IntegrationTestBase {
    private static final String FLOAT = "Float: equals doesn't use Float.compare for field";
    private static final String DOUBLE = "Double: equals doesn't use Double.compare for field";

    @Test
    public void fail_whenFloatsAreComparedByReference() {
        expectFailure(FLOAT, "f");
        EqualsVerifier.forClass(ComparePrimitiveFloatsByReference.class)
                .verify();
    }

    @Test
    public void fail_whenObjectFloatsAreComparedByReference() {
        expectFailure(FLOAT, "f");
        EqualsVerifier.forClass(CompareObjectFloatByReference.class)
                .verify();
    }

    @Test
    public void succeed_whenFloatsAreComparedWithFloatCompare() {
        EqualsVerifier.forClass(CompareFloatCorrectly.class)
                .verify();
    }

    @Test
    public void fail_whenDoublesAreComparedByReference() {
        expectFailure(DOUBLE, "d");
        EqualsVerifier.forClass(ComparePrimitiveDoubleByReference.class)
                .verify();
    }

    @Test
    public void fail_whenObjectDoublesAreComparedByReference() {
        expectFailure(DOUBLE, "d");
        EqualsVerifier.forClass(CompareObjectDoubleByReference.class)
                .verify();
    }

    @Test
    public void succeed_whenDoublesAreComparedWithDoubleCompare() {
        EqualsVerifier.forClass(CompareDoubleCorrectly.class)
                .verify();
    }

    static final class ComparePrimitiveFloatsByReference {
        private final float f;

        public ComparePrimitiveFloatsByReference(float f) { this.f = f; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ComparePrimitiveFloatsByReference)) {
                return false;
            }
            return f == ((ComparePrimitiveFloatsByReference)obj).f;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class CompareObjectFloatByReference {
        private final Float f;

        public CompareObjectFloatByReference(Float f) { this.f = f; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareObjectFloatByReference)) {
                return false;
            }
            return f == ((CompareObjectFloatByReference)obj).f;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class CompareFloatCorrectly {
        private final float f;

        public CompareFloatCorrectly(float f) { this.f = f; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareFloatCorrectly)) {
                return false;
            }
            return Float.compare(f, ((CompareFloatCorrectly)obj).f) == 0;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class ComparePrimitiveDoubleByReference {
        private final double d;

        public ComparePrimitiveDoubleByReference(double d) { this.d = d; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ComparePrimitiveDoubleByReference)) {
                return false;
            }
            return d == ((ComparePrimitiveDoubleByReference)obj).d;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class CompareObjectDoubleByReference {
        private final Double d;

        public CompareObjectDoubleByReference(Double d) { this.d = d; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareObjectDoubleByReference)) {
                return false;
            }
            return d == ((CompareObjectDoubleByReference)obj).d;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class CompareDoubleCorrectly {
        private final double d;

        public CompareDoubleCorrectly(double d) { this.d = d; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CompareDoubleCorrectly)) {
                return false;
            }
            return Double.compare(d, ((CompareDoubleCorrectly)obj).d) == 0;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
