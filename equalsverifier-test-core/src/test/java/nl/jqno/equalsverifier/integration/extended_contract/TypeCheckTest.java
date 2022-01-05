package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class TypeCheckTest {

    @Test
    public void fail_whenEqualsReturnsTrueForACompletelyUnrelatedType() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(WrongTypeCheck.class).verify())
            .assertFailure()
            .assertMessageContains("Type-check: equals returns true for an unrelated type.");
    }

    @Test
    public void fail_whenEqualsDoesNotTypeCheck() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(NoTypeCheck.class).verify())
            .assertFailure()
            .assertCause(ClassCastException.class)
            .assertMessageContains("Type-check: equals throws ClassCastException");
    }

    @Test
    public void fail_whenEqualsDoesNotTypeCheckAndThrowsAnExceptionOtherThanClassCastException() {
        ExpectedException
            .when(() ->
                EqualsVerifier.forClass(NoTypeCheckButNoClassCastExceptionEither.class).verify()
            )
            .assertFailure()
            .assertCause(IllegalStateException.class)
            .assertMessageContains("Type-check: equals throws IllegalStateException");
    }

    static final class WrongTypeCheck {

        private final int i;

        public WrongTypeCheck(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof WrongTypeCheck)) {
                return true;
            }
            return i == ((WrongTypeCheck) obj).i;
        }
    }

    static final class NoTypeCheck {

        private final int i;

        public NoTypeCheck(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return i == ((NoTypeCheck) obj).i;
        }
    }

    static final class NoTypeCheckButNoClassCastExceptionEither {

        private final int i;

        public NoTypeCheckButNoClassCastExceptionEither(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            try {
                if (obj == null) {
                    return false;
                }
                return i == ((NoTypeCheckButNoClassCastExceptionEither) obj).i;
            } catch (ClassCastException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
