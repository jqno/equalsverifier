/*
 * Copyright 2009-2010, 2013-2014 Jan Ouwens
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
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class NullFieldsTest extends IntegrationTestBase {
    private static final String NON_NULLITY = "Non-nullity";
    private static final String EQUALS = "equals throws NullPointerException";
    private static final String HASHCODE = "hashCode throws NullPointerException";
    private static final String ON_FIELD = "on field";

    @Test
    public void fail_whenEqualsThrowsNpeOnThissField() {
        expectFailureWithCause(NullPointerException.class, NON_NULLITY, EQUALS, ON_FIELD, "color");
        EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsThrowsNpeOnOthersField() {
        expectFailureWithCause(NullPointerException.class, NON_NULLITY, EQUALS, ON_FIELD, "color");
        EqualsVerifier.forClass(EqualsThrowsNpeOnOther.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsThrowsNpeOnStaticField() {
        expectFailureWithCause(NullPointerException.class, NON_NULLITY, EQUALS, ON_FIELD, "color");
        EqualsVerifier.forClass(EqualsThrowsNpeOnStatic.class)
                .verify();
    }

    @Test
    public void fail_whenHashCodeThrowsNpe() {
        expectFailureWithCause(NullPointerException.class, NON_NULLITY, HASHCODE, ON_FIELD, "color");
        EqualsVerifier.forClass(HashCodeThrowsNpe.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsThrowsNpeOnThissField_givenExamples() {
        EqualsThrowsNpeOnThis blue = new EqualsThrowsNpeOnThis(Color.BLUE);
        EqualsThrowsNpeOnThis yellow = new EqualsThrowsNpeOnThis(Color.YELLOW);

        EqualsVerifier.forExamples(blue, yellow)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenEqualsThrowsNpeOnThissField_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(EqualsThrowsNpeOnThis.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenEqualsTestFieldWhichThrowsNpe() {
        EqualsVerifier.forClass(CheckedDeepNullA.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsThrowsNpeOnFieldWhichAlsoThrowsNpe_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(DeepNullA.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenDoingASanityCheckOnTheFieldUsedInThePreviousTests_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(DeepNullB.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenConstantFieldIsNull() {
        EqualsVerifier.forClass(ConstantFieldIsNull.class)
                .verify();
    }

    static final class EqualsThrowsNpeOnThis {
        private final Color color;

        public EqualsThrowsNpeOnThis(Color color) { this.color = color; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrowsNpeOnThis)) {
                return false;
            }
            EqualsThrowsNpeOnThis p = (EqualsThrowsNpeOnThis)obj;
            return color.equals(p.color);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class EqualsThrowsNpeOnOther {
        private final Color color;

        public EqualsThrowsNpeOnOther(Color color) { this.color = color; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrowsNpeOnOther)) {
                return false;
            }
            EqualsThrowsNpeOnOther p = (EqualsThrowsNpeOnOther)obj;
            return p.color.equals(color);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class EqualsThrowsNpeOnStatic {
        private static Color color = Color.INDIGO;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EqualsThrowsNpeOnStatic)) {
                return false;
            }
            EqualsThrowsNpeOnStatic p = (EqualsThrowsNpeOnStatic)obj;
            return color.equals(p.color);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class HashCodeThrowsNpe {
        private final Color color;

        public HashCodeThrowsNpe(Color color) { this.color = color; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            return color.hashCode();
        }

        @Override
        public String toString() {
            //Object.toString calls hashCode()
            return "";
        }
    }

    static final class CheckedDeepNullA {
        private final DeepNullB b;

        public CheckedDeepNullA(DeepNullB b) { this.b = b; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class DeepNullA {
        private final DeepNullB b;

        public DeepNullA(DeepNullB b) {
            if (b == null) {
                throw new NullPointerException("b");
            }

            this.b = b;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class DeepNullB {
        private final Object o;

        public DeepNullB(Object o) {
            if (o == null) {
                throw new NullPointerException("o");
            }

            this.o = o;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class ConstantFieldIsNull {
        private static final String NULL_CONSTANT = null;
        private final Object o;

        public ConstantFieldIsNull(Object o) {
            this.o = o;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
