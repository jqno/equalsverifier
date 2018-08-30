package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;

public class OutputTest extends ExpectedExceptionTestBase {
    private static final String SEE_ALSO = "For more information, go to";
    private static final String WIKIPAGE_URL = "http://www.jqno.nl/equalsverifier/errormessages";
    private static final String MESSAGE = "a message for an exception";

    @Test
    public void messageIsValid_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        expectMessageIsValidFor(Point.class);
        expectCause(AssertionException.class);

        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    public void messageIsValidAndCauseHasCause_whenEqualsVerifierFails_givenOriginalExceptionHasACause() {
        expectMessageIsValidFor(AssertionExceptionWithCauseThrower.class);
        expectMessageContains(MESSAGE);
        expectMessageDoesNotContain(NullPointerException.class.getSimpleName());
        expectCause(AssertionException.class);
        expectCause(NullPointerException.class);

        EqualsVerifier.forClass(AssertionExceptionWithCauseThrower.class).verify();
    }

    @Test
    public void originalMessageIsPresentInOutput_whenEqualsVerifierFails_givenOriginalExceptionHasAMessage() {
        expectMessageIsValidFor(UnsupportedOperationExceptionWithMessageThrower.class);
        expectMessageContains(UnsupportedOperationException.class.getSimpleName(), MESSAGE);
        expectMessageDoesNotContain("null");
        expectCause(UnsupportedOperationException.class, MESSAGE);

        EqualsVerifier.forClass(UnsupportedOperationExceptionWithMessageThrower.class).verify();
    }

    @Test
    public void messageIsValidAndDoesNotContainStringNull_whenEqualsVerifierFails_givenOriginalExceptionIsBare() {
        expectMessageIsValidFor(IllegalStateExceptionThrower.class);
        expectMessageContains(IllegalStateException.class.getSimpleName());
        expectMessageDoesNotContain("null");
        expectCause(IllegalStateException.class);

        EqualsVerifier.forClass(IllegalStateExceptionThrower.class).verify();
    }

    @Test
    public void noStackOverflowErrorIsThrown_whenClassIsARecursiveDatastructure() {
        expectMessageIsValidFor(Node.class);
        expectCauseIsnt(StackOverflowError.class);

        EqualsVerifier.forClass(Node.class).verify();
    }

    private void expectMessageIsValidFor(Class<?> type) {
        expectMessageContains(type.getSimpleName());
        expectMessageContains(SEE_ALSO, WIKIPAGE_URL);
    }

    private void expectMessageContains(String... contains) {
        for (String s : contains) {
            thrown.expectMessage(s);
        }
    }

    private void expectMessageDoesNotContain(String... doesNotContain) {
        for (String s : doesNotContain) {
            thrown.expect(not(s));
        }
    }

    private void expectCause(Class<? extends Throwable> cause, String... message) {
        expectFailureWithCause(cause, message);
    }

    private void expectCauseIsnt(Class<? extends Throwable> notCause) {
        expectFailureWithoutCause(notCause);
    }

    private static class AssertionExceptionWithCauseThrower {
        @Override
        public boolean equals(Object obj) {
            Throwable cause = new NullPointerException();
            throw new AssertionException(Formatter.of(MESSAGE), cause);
        }
    }

    private static class UnsupportedOperationExceptionWithMessageThrower {
        @Override
        public boolean equals(Object obj) {
            throw new UnsupportedOperationException(MESSAGE);
        }
    }

    private static class IllegalStateExceptionThrower {
        @Override
        public boolean equals(Object obj) {
            throw new IllegalStateException();
        }
    }
}
