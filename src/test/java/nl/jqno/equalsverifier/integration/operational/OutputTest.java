package nl.jqno.equalsverifier.integration.operational;

import static org.hamcrest.CoreMatchers.not;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.Test;

public class OutputTest extends ExpectedExceptionTestBase {
    private static final String SEE_ALSO = "For more information, go to";
    private static final String WEBSITE_URL = "http://www.jqno.nl/equalsverifier/errormessages";
    private static final String MESSAGE = "a message for an exception";

    @Test
    public void
            messageIsValidForSingleType_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        expectMessageIsValidFor(Point.class);
        expectFailureWithCause(AssertionException.class);

        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    public void
            messageIsValidForMultipleTypes_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        expectMessageIsValidFor(Point.class);
        expectFailure("---");

        EqualsVerifier.forClasses(A.class, Point.class).verify();
    }

    @Test
    public void errorDescriptionAppearsOnlyAtTopOfStacktrace_notInOneOfItsCauses() {
        expectMessageContains("Subclass");
        expectCauseMessageDoesNotContain("Subclass");

        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    public void
            messageIsValidAndCauseHasCause_whenEqualsVerifierFails_givenOriginalExceptionHasACause() {
        expectMessageIsValidFor(AssertionExceptionWithCauseThrower.class);
        expectMessageContains(MESSAGE);
        expectMessageDoesNotContain(NullPointerException.class.getSimpleName());
        expectFailureWithCause(AssertionException.class);
        expectFailureWithCause(NullPointerException.class);

        EqualsVerifier.forClass(AssertionExceptionWithCauseThrower.class).verify();
    }

    @Test
    public void
            originalMessageIsPresentInOutput_whenEqualsVerifierFails_givenOriginalExceptionHasAMessage() {
        expectMessageIsValidFor(UnsupportedOperationExceptionWithMessageThrower.class);
        expectMessageContains(UnsupportedOperationException.class.getSimpleName(), MESSAGE);
        expectMessageDoesNotContain("null");
        expectFailureWithCause(UnsupportedOperationException.class, MESSAGE);

        EqualsVerifier.forClass(UnsupportedOperationExceptionWithMessageThrower.class).verify();
    }

    @Test
    public void
            messageIsValidAndDoesNotContainStringNull_whenEqualsVerifierFails_givenOriginalExceptionIsBare() {
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
        expectMessageContains(SEE_ALSO, WEBSITE_URL);
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
