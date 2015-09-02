/*
 * Copyright 2013-2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.basic_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Objects;

public class TransitivityTest extends IntegrationTestBase {
    @Test
    public void succeed_whenEqualityForTwoFieldsIsCombinedUsingAND() {
        EqualsVerifier.forClass(TwoFieldsUsingAND.class)
                .verify();
    }

    @Test
    public void fail_whenEqualityForTwoFieldsIsCombinedUsingOR() {
        expectFailure("Transitivity", "two of these three instances are equal to each other, so the third one should be, too", TwoFieldsUsingOR.class.getSimpleName());
        EqualsVerifier.forClass(TwoFieldsUsingOR.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void succeed_whenEqualityForThreeFieldsIsCombinedUsingAND() {
        EqualsVerifier.forClass(ThreeFieldsUsingAND.class)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingOR() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(ThreeFieldsUsingOR.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingOR_givenRelaxedEqualExamples() {
        ThreeFieldsUsingOR one = new ThreeFieldsUsingOR("a", "1", "alpha");
        ThreeFieldsUsingOR two = new ThreeFieldsUsingOR("b", "1", "alpha");
        ThreeFieldsUsingOR three = new ThreeFieldsUsingOR("c", "1", "alpha");
        ThreeFieldsUsingOR other = new ThreeFieldsUsingOR("d", "4", "delta");

        expectFailure("Transitivity");
        EqualsVerifier.forRelaxedEqualExamples(one, two, three)
                .andUnequalExample(other)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingANDAndOR() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(ThreeFieldsUsingANDOR.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForThreeFieldsIsCombinedUsingORAndAND() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(ThreeFieldsUsingORAND.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForFiveFieldsIsCombinedUsingOR() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(FiveFieldsUsingOR.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualityForFiveFieldsIsCombinedUsingANDsAndORs() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(FiveFieldsUsingANDsAndORs.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Ignore("This class is not transitive, and it should fail. See issue 78.")
    @Test
    public void fail_whenInstancesAreEqualIfAtLeastTwoFieldsAreEqual() {
        expectFailure("Transitivity");
        EqualsVerifier.forClass(AtLeast2FieldsAreEqual.class)
                .verify();
    }

    static final class TwoFieldsUsingAND {
        private final String f;
        private final String g;

        public TwoFieldsUsingAND(String f, String g) { this.f = f; this.g = g; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TwoFieldsUsingAND)) {
                return false;
            }
            TwoFieldsUsingAND other = (TwoFieldsUsingAND)obj;
            return Objects.equals(f, other.f) && Objects.equals(g, other.g);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class TwoFieldsUsingOR {
        private final String f;
        private final String g;

        public TwoFieldsUsingOR(String f, String g) { this.f = f; this.g = g; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TwoFieldsUsingOR)) {
                return false;
            }
            TwoFieldsUsingOR other = (TwoFieldsUsingOR)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class ThreeFieldsUsingAND {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingAND(String f, String g, String h) { this.f = f; this.g = g; this.h = h; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingAND)) {
                return false;
            }
            ThreeFieldsUsingAND other = (ThreeFieldsUsingAND)obj;
            return Objects.equals(f, other.f) && Objects.equals(g, other.g) && Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class ThreeFieldsUsingOR {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingOR(String f, String g, String h) { this.f = f; this.g = g; this.h = h; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingOR)) {
                return false;
            }
            ThreeFieldsUsingOR other = (ThreeFieldsUsingOR)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) || Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class ThreeFieldsUsingANDOR {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingANDOR(String f, String g, String h) { this.f = f; this.g = g; this.h = h; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingANDOR)) {
                return false;
            }
            ThreeFieldsUsingANDOR other = (ThreeFieldsUsingANDOR)obj;
            return Objects.equals(f, other.f) && Objects.equals(g, other.g) || Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class ThreeFieldsUsingORAND {
        private final String f;
        private final String g;
        private final String h;

        public ThreeFieldsUsingORAND(String f, String g, String h) { this.f = f; this.g = g; this.h = h; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ThreeFieldsUsingORAND)) {
                return false;
            }
            ThreeFieldsUsingORAND other = (ThreeFieldsUsingORAND)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) && Objects.equals(h, other.h);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class FiveFieldsUsingOR {
        private final String f;
        private final String g;
        private final String h;
        private final String i;
        private final String j;

        public FiveFieldsUsingOR(String f, String g, String h, String i, String j) { this.f = f; this.g = g; this.h = h; this.i = i; this.j = j; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FiveFieldsUsingOR)) {
                return false;
            }
            FiveFieldsUsingOR other = (FiveFieldsUsingOR)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) ||
                    Objects.equals(h, other.h) || Objects.equals(i, other.i) || Objects.equals(j, other.j);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class FiveFieldsUsingANDsAndORs {
        private final String f;
        private final String g;
        private final String h;
        private final String i;
        private final String j;

        public FiveFieldsUsingANDsAndORs(String f, String g, String h, String i, String j) { this.f = f; this.g = g; this.h = h; this.i = i; this.j = j; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FiveFieldsUsingANDsAndORs)) {
                return false;
            }
            FiveFieldsUsingANDsAndORs other = (FiveFieldsUsingANDsAndORs)obj;
            return Objects.equals(f, other.f) || Objects.equals(g, other.g) &&
                    Objects.equals(h, other.h) || Objects.equals(i, other.i) && Objects.equals(j, other.j);
        }

        @Override public int hashCode() { return 42; }
    }

    static final class AtLeast2FieldsAreEqual {
        private final int i;
        private final int j;
        private final int k;
        private final int l;

        public AtLeast2FieldsAreEqual(int i, int j, int k, int l) { this.i = i; this.j = j; this.k = k; this.l = l; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AtLeast2FieldsAreEqual)) {
                return false;
            }
            AtLeast2FieldsAreEqual other = (AtLeast2FieldsAreEqual) obj;
            int x = 0;
            if (i == other.i) x++;
            if (j == other.j) x++;
            if (k == other.k) x++;
            if (l == other.l) x++;
            return x >= 2;
        }

        @Override public int hashCode() { return 42; }
    }
}
