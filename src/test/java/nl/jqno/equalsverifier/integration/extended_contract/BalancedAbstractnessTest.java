/*
 * Copyright 2011, 2014 Jan Ouwens
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

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class BalancedAbstractnessTest extends IntegrationTestBase {
    private static final String ABSTRACT_DELEGATION = "Abstract delegation";
    private static final String BOTH_ARE_ABSTRACT = "equals and hashCode methods are both abstract";
    private static final String EQUALS_IS_ABSTRACT = "equals method is abstract";
    private static final String HASHCODE_IS_ABSTRACT = "hashCode method is abstract";
    private static final String EQUALS_IS_NOT = "but equals is not";
    private static final String HASHCODE_IS_NOT = "but hashCode is not";

    @Test
    public void fail_whenBothEqualsAndHashCodeAreAbstract() {
        expectFailure(BOTH_ARE_ABSTRACT, AbstractBoth.class.getSimpleName());
        EqualsVerifier.forClass(AbstractBoth.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsAbstract() {
        expectFailure(EQUALS_IS_ABSTRACT, HASHCODE_IS_NOT, AbstractEquals.class.getSimpleName());
        EqualsVerifier.forClass(AbstractEquals.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .verify();
    }

    @Test
    public void fail_whenHashCodeIsAbstract() {
        expectFailure(HASHCODE_IS_ABSTRACT, EQUALS_IS_NOT, AbstractHashCode.class.getSimpleName());
        EqualsVerifier.forClass(AbstractHashCode.class)
                .verify();
    }

    @Test
    public void succeed_whenBothAreAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractBoth.class)
                .verify();
    }

    @Test
    public void fail_whenOnlyEqualsIsAbstractInSuperclass() {
        expectFailure(ABSTRACT_DELEGATION, EQUALS_IS_ABSTRACT, HASHCODE_IS_NOT, AbstractEqualsButNotHashCode.class.getSimpleName());
        EqualsVerifier.forClass(SubclassOfAbstractEqualsButNotHashCode.class)
                .verify();
    }

    @Test
    public void fail_whenOnlyHashCodeIsAbstractInSuperclass() {
        expectFailure(ABSTRACT_DELEGATION, HASHCODE_IS_ABSTRACT, EQUALS_IS_NOT, AbstractHashCodeButNotEquals.class.getSimpleName());
        EqualsVerifier.forClass(SubclassOfAbstractHashCodeButNotEquals.class)
                .verify();
    }

    @Test
    public void succeed_whenBothAreAbstractInSuperclassOfSuperclass() {
        EqualsVerifier.forClass(SubclassOfSubclassOfAbstractBoth.class)
                .verify();
    }

    abstract static class AbstractBoth {
        @Override
        public abstract boolean equals(Object obj);
        @Override
        public abstract int hashCode();
    }

    abstract static class AbstractEquals {
        private int i;
        public AbstractEquals(int i) { this.i = i; }

        @Override
        public abstract boolean equals(Object obj);

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    abstract static class AbstractHashCode {
        private int i;
        public AbstractHashCode(int i) { this.i = i; }
        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public abstract int hashCode();
    }

    static final class SubclassOfAbstractBoth extends AbstractBoth {
        private final int foo;

        public SubclassOfAbstractBoth(int foo) { this.foo = foo; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static abstract class AbstractEqualsButNotHashCode {
        @Override
        public abstract boolean equals(Object obj);
    }

    static class SubclassOfAbstractEqualsButNotHashCode extends AbstractEqualsButNotHashCode {
        private final int foo;

        public SubclassOfAbstractEqualsButNotHashCode(int foo) { this.foo = foo; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static abstract class AbstractHashCodeButNotEquals {
        @Override
        public abstract int hashCode();
    }

    static class SubclassOfAbstractHashCodeButNotEquals extends AbstractHashCodeButNotEquals {
        private final int foo;

        public SubclassOfAbstractHashCodeButNotEquals(int foo) { this.foo = foo; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static abstract class IntermediateSubclassOfAbstractBoth extends AbstractBoth {}

    static final class SubclassOfSubclassOfAbstractBoth extends IntermediateSubclassOfAbstractBoth {
        private final int foo;

        public SubclassOfSubclassOfAbstractBoth(int foo) { this.foo = foo; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
