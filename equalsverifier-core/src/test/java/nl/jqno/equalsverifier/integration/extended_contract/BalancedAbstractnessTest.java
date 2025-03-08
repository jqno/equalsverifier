package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class BalancedAbstractnessTest {

    private static final String BOTH_ARE_ABSTRACT = "equals and hashCode methods are both abstract";
    private static final String EQUALS_IS_ABSTRACT = "equals method is abstract";
    private static final String HASHCODE_IS_ABSTRACT = "hashCode method is abstract";
    private static final String EQUALS_IS_NOT = "but equals is not";
    private static final String HASHCODE_IS_NOT = "but hashCode is not";
    private static final String BOTH_SHOULD_BE_CONCRETE = "Both should be concrete";

    @Test
    void fail_whenBothEqualsAndHashCodeAreAbstract() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(AbstractBoth.class)
                            .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                            .verify())
                .assertFailure()
                .assertMessageContains(BOTH_ARE_ABSTRACT, AbstractBoth.class.getSimpleName());
    }

    @Test
    void fail_whenEqualsIsAbstract() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(AbstractEquals.class)
                            .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    EQUALS_IS_ABSTRACT,
                    HASHCODE_IS_NOT,
                    BOTH_SHOULD_BE_CONCRETE,
                    AbstractEquals.class.getSimpleName());
    }

    @Test
    void fail_whenHashCodeIsAbstract() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(AbstractHashCode.class).verify())
                .assertFailure()
                .assertMessageContains(
                    HASHCODE_IS_ABSTRACT,
                    EQUALS_IS_NOT,
                    BOTH_SHOULD_BE_CONCRETE,
                    AbstractHashCode.class.getSimpleName());
    }

    @Test
    void succeed_whenBothAreAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractBoth.class).verify();
    }

    @Test
    void succeed_whenOnlyEqualsIsAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractEqualsButNotHashCode.class).verify();
    }

    @Test
    void succeed_whenOnlyHashCodeIsAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractHashCodeButNotEquals.class).verify();
    }

    @Test
    void succeed_whenBothAreAbstractInSuperclassOfSuperclass() {
        EqualsVerifier.forClass(SubclassOfSubclassOfAbstractBoth.class).verify();
    }

    abstract static class AbstractBoth {

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    abstract static class AbstractEquals {

        private int i;

        public AbstractEquals(int i) {
            this.i = i;
        }

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    abstract static class AbstractHashCode {

        private int i;

        public AbstractHashCode(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AbstractHashCode other && Objects.equals(i, other.i);
        }

        @Override
        public abstract int hashCode();
    }

    static final class SubclassOfAbstractBoth extends AbstractBoth {

        private final int foo;

        public SubclassOfAbstractBoth(int foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SubclassOfAbstractBoth other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    abstract static class AbstractEqualsButNotHashCode {

        @Override
        public abstract boolean equals(Object obj);
    }

    static final class SubclassOfAbstractEqualsButNotHashCode extends AbstractEqualsButNotHashCode {

        private final int foo;

        public SubclassOfAbstractEqualsButNotHashCode(int foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SubclassOfAbstractEqualsButNotHashCode other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    abstract static class AbstractHashCodeButNotEquals {

        @Override
        public abstract int hashCode();
    }

    static final class SubclassOfAbstractHashCodeButNotEquals extends AbstractHashCodeButNotEquals {

        private final int foo;

        public SubclassOfAbstractHashCodeButNotEquals(int foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SubclassOfAbstractHashCodeButNotEquals other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }

    abstract static class IntermediateSubclassOfAbstractBoth extends AbstractBoth {}

    static final class SubclassOfSubclassOfAbstractBoth extends IntermediateSubclassOfAbstractBoth {

        private final int foo;

        public SubclassOfSubclassOfAbstractBoth(int foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SubclassOfSubclassOfAbstractBoth other && Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo);
        }
    }
}
