package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

public class TypeCheckTest extends ExpectedExceptionTestBase {
    @Test
    public void fail_whenEqualsReturnsTrueForACompletelyUnrelatedType() {
        expectFailure("Type-check: equals returns true for an unrelated type.");
        EqualsVerifier.forClass(WrongTypeCheck.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesNotTypeCheck() {
        expectFailureWithCause(ClassCastException.class, "Type-check: equals throws ClassCastException");
        EqualsVerifier.forClass(NoTypeCheck.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesNotTypeCheckAndThrowsAnExceptionOtherThanClassCastException() {
        expectFailureWithCause(IllegalStateException.class, "Type-check: equals throws IllegalStateException");
        EqualsVerifier.forClass(NoTypeCheckButNoClassCastExceptionEither.class)
                .verify();
    }

    static final class WrongTypeCheck {
        private final int i;

        public WrongTypeCheck(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof WrongTypeCheck)) {
                return true;
            }
            return i == ((WrongTypeCheck)obj).i;
        }
    }

    static final class NoTypeCheck {
        private final int i;

        public NoTypeCheck(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return i == ((NoTypeCheck)obj).i;
        }
    }

    static final class NoTypeCheckButNoClassCastExceptionEither {
        private final int i;

        public NoTypeCheckButNoClassCastExceptionEither(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            try {
                if (obj == null) {
                    return false;
                }
                return i == ((NoTypeCheckButNoClassCastExceptionEither)obj).i;
            }
            catch (ClassCastException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
