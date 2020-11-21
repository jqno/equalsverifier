package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.jupiter.api.Test;

public class OutputTest {
    private static final String SEE_ALSO = "For more information, go to";
    private static final String WEBSITE_URL = "http://www.jqno.nl/equalsverifier/errormessages";
    private static final String MESSAGE = "a message for an exception";

    @Test
    public void
            messageIsValidForSingleType_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Point.class).verify())
                .assertCause(AssertionException.class)
                .assertMessageContains(Point.class.getSimpleName(), SEE_ALSO, WEBSITE_URL);
    }

    @Test
    public void
            messageIsValidForMultipleTypes_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        ExpectedException.when(() -> EqualsVerifier.forClasses(A.class, Point.class).verify())
                .assertMessageContains(Point.class.getSimpleName(), SEE_ALSO, WEBSITE_URL, "---");
    }

    @Test
    public void errorDescriptionAppearsOnlyAtTopOfStacktrace_notInOneOfItsCauses() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Point.class).verify())
                .assertMessageContains("Subclass")
                .assertCauseMessageDoesNotContain("Subclass");
    }

    @Test
    public void
            messageIsValidAndCauseHasCause_whenEqualsVerifierFails_givenOriginalExceptionHasACause() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(AssertionExceptionWithCauseThrower.class)
                                        .verify())
                .assertMessageContains(AssertionExceptionWithCauseThrower.class.getSimpleName())
                .assertMessageContains(SEE_ALSO, WEBSITE_URL, MESSAGE)
                .assertMessageDoesNotContain(NullPointerException.class.getSimpleName())
                .assertCause(AssertionException.class)
                .assertCause(NullPointerException.class);
    }

    @Test
    public void
            originalMessageIsPresentInOutput_whenEqualsVerifierFails_givenOriginalExceptionHasAMessage() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(
                                                UnsupportedOperationExceptionWithMessageThrower
                                                        .class)
                                        .verify())
                .assertMessageContains(
                        UnsupportedOperationExceptionWithMessageThrower.class.getSimpleName())
                .assertMessageContains(UnsupportedOperationException.class.getSimpleName())
                .assertMessageContains(SEE_ALSO, WEBSITE_URL, MESSAGE)
                .assertMessageDoesNotContain("null")
                .assertCause(UnsupportedOperationException.class)
                .assertCauseMessageContains(MESSAGE);
    }

    @Test
    public void
            messageIsValidAndDoesNotContainStringNull_whenEqualsVerifierFails_givenOriginalExceptionIsBare() {
        ExpectedException.when(
                        () -> EqualsVerifier.forClass(IllegalStateExceptionThrower.class).verify())
                .assertMessageContains(IllegalStateExceptionThrower.class.getSimpleName())
                .assertMessageContains(SEE_ALSO, WEBSITE_URL, "<no message>")
                .assertMessageDoesNotContain("null")
                .assertCause(IllegalStateException.class);
    }

    @Test
    public void noStackOverflowErrorIsThrown_whenClassIsARecursiveDatastructure() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Node.class).verify())
                .assertMessageContains(Node.class.getSimpleName(), SEE_ALSO, WEBSITE_URL)
                .assertNotCause(StackOverflowError.class);
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
