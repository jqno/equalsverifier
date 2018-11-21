package equalsverifier.integration.operational;

import equalsverifier.EqualsVerifier;
import equalsverifier.utils.exceptions.AssertionException;
import equalsverifier.utils.Formatter;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.Point;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;

public class OutputTest extends ExpectedExceptionTestBase {
    private static final String SEE_ALSO = "For more information, go to";
    private static final String WIKIPAGE_URL = "http://www.jqno.nl/equalsverifier/errormessages";
    private static final String MESSAGE = "a message for an exception";

    @Test
    public void messageIsValid_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        expectMessageIsValidFor(Point.class);
        expectFailureWithCause(AssertionException.class);

        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    public void errorDescriptionAppearsOnlyAtTopOfStacktrace_notInOneOfItsCauses() {
        expectMessageContains("Subclass");
        expectCauseMessageDoesNotContain("Subclass");

        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    public void messageIsValidAndCauseHasCause_whenEqualsVerifierFails_givenOriginalExceptionHasACause() {
        expectMessageIsValidFor(AssertionExceptionWithCauseThrower.class);
        expectMessageContains(MESSAGE);
        expectMessageDoesNotContain(NullPointerException.class.getSimpleName());
        expectFailureWithCause(AssertionException.class);
        expectFailureWithCause(NullPointerException.class);

        EqualsVerifier.forClass(AssertionExceptionWithCauseThrower.class).verify();
    }

    @Test
    public void originalMessageIsPresentInOutput_whenEqualsVerifierFails_givenOriginalExceptionHasAMessage() {
        expectMessageIsValidFor(UnsupportedOperationExceptionWithMessageThrower.class);
        expectMessageContains(UnsupportedOperationException.class.getSimpleName(), MESSAGE);
        expectMessageDoesNotContain("null");
        expectFailureWithCause(UnsupportedOperationException.class, MESSAGE);

        EqualsVerifier.forClass(UnsupportedOperationExceptionWithMessageThrower.class).verify();
    }

    @Test
    public void messageIsValidAndDoesNotContainStringNull_whenEqualsVerifierFails_givenOriginalExceptionIsBare() {
        expectMessageIsValidFor(IllegalStateExceptionThrower.class);
        expectMessageContains(IllegalStateException.class.getSimpleName());
        expectMessageDoesNotContain("null");
        expectFailureWithCause(IllegalStateException.class);

        EqualsVerifier.forClass(IllegalStateExceptionThrower.class).verify();
    }

    @Test
    public void noStackOverflowErrorIsThrown_whenClassIsARecursiveDatastructure() {
        expectMessageIsValidFor(Node.class);
        expectFailureWithoutCause(StackOverflowError.class);

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
