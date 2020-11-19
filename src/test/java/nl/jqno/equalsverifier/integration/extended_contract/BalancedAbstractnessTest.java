package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class BalancedAbstractnessTest {
    private static final String ABSTRACT_DELEGATION = "Abstract delegation";
    private static final String BOTH_ARE_ABSTRACT = "equals and hashCode methods are both abstract";
    private static final String EQUALS_IS_ABSTRACT = "equals method is abstract";
    private static final String HASHCODE_IS_ABSTRACT = "hashCode method is abstract";
    private static final String EQUALS_IS_NOT = "but equals is not";
    private static final String HASHCODE_IS_NOT = "but hashCode is not";
    private static final String BOTH_SHOULD_BE_CONCRETE = "Both should be concrete";
    private static final String BOTH_SHOULD_BE_SAME = "Both should be either abstract or concrete";

    @Test
    public void fail_whenBothEqualsAndHashCodeAreAbstract() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(AbstractBoth.class)
                                        .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                                        .verify())
                .assertFailure()
                .assertMessageContains(BOTH_ARE_ABSTRACT, AbstractBoth.class.getSimpleName());
    }

    @Test
    public void fail_whenEqualsIsAbstract() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(AbstractEquals.class)
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
    public void fail_whenHashCodeIsAbstract() {
        ExpectedException.when(() -> EqualsVerifier.forClass(AbstractHashCode.class).verify())
                .assertFailure()
                .assertMessageContains(
                        HASHCODE_IS_ABSTRACT,
                        EQUALS_IS_NOT,
                        BOTH_SHOULD_BE_CONCRETE,
                        AbstractHashCode.class.getSimpleName());
    }

    @Test
    public void succeed_whenBothAreAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractBoth.class).verify();
    }

    @Test
    public void succeed_whenOnlyEqualsIsAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractEqualsButNotHashCode.class).verify();
    }

    @Test
    public void succeed_whenOnlyHashCodeIsAbstractInSuperclass() {
        EqualsVerifier.forClass(SubclassOfAbstractHashCodeButNotEquals.class).verify();
    }

    @Test
    public void succeed_whenBothAreAbstractInSuperclassOfSuperclass() {
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
            return defaultHashCode(this);
        }
    }

    abstract static class AbstractHashCode {
        private int i;

        public AbstractHashCode(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
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
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
