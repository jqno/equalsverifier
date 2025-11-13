package nl.jqno.equalsverifier.integration.operational;

import java.util.regex.Pattern;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.MutablePoint;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.jupiter.api.Test;

class OutputTest {

    private static final String SEE_ALSO = "For more information, go to";
    private static final String WEBSITE_URL = "https://www.jqno.nl/equalsverifier/errormessages";
    private static final String CLASSPATH = "running on classpath";
    private static final String MESSAGE = "a message for an exception";
    private static final Pattern SUFFIX =
            Pattern.compile("\\(EqualsVerifier .*, JDK .* running on (class|module)path, on .*. Mockito: .*\\)");

    @Test
    void messageIsValidForSingleType_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(Point.class).verify())
                .assertCause(AssertionException.class)
                .assertMessageContains(Point.class.getSimpleName(), SEE_ALSO, WEBSITE_URL, CLASSPATH)
                .assertMessageMatches(SUFFIX);
    }

    @Test
    void messageIsValidForMultipleTypes_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(Point.class, MutablePoint.class).verify())
                .assertMessageContains(Point.class.getSimpleName())
                .assertMessageContains("---")
                .assertMessageContainsOnce(SEE_ALSO)
                .assertMessageContainsOnce(WEBSITE_URL)
                .assertMessageContainsOnce(CLASSPATH)
                .assertMessageMatches(SUFFIX);
    }

    @Test
    void errorDescriptionAppearsOnlyAtTopOfStacktrace_notInOneOfItsCauses() {
        ExpectedException.when(() -> EqualsVerifier.forClass(Point.class).verify()).assertMessageContains("Subclass");
    }

    @Test
    void messageIsValidAndCauseHasCause_whenEqualsVerifierFails_givenOriginalExceptionHasACause() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(AssertionExceptionWithCauseThrower.class).verify())
                .assertMessageContains(AssertionExceptionWithCauseThrower.class.getSimpleName())
                .assertMessageContains(SEE_ALSO, WEBSITE_URL, CLASSPATH, MESSAGE)
                .assertMessageMatches(SUFFIX)
                .assertMessageDoesNotContain(NullPointerException.class.getSimpleName())
                .assertCause(AssertionException.class)
                .assertCause(NullPointerException.class);
    }

    @Test
    void originalMessageIsPresentInOutput_whenEqualsVerifierFails_givenOriginalExceptionHasAMessage() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UnsupportedOperationExceptionWithMessageThrower.class).verify())
                .assertMessageContains(UnsupportedOperationExceptionWithMessageThrower.class.getSimpleName())
                .assertMessageContains(UnsupportedOperationException.class.getSimpleName())
                .assertMessageContains(SEE_ALSO, WEBSITE_URL, MESSAGE)
                .assertMessageMatches(SUFFIX)
                .assertMessageDoesNotContainAfterRemove("EqualsVerifier null", "null")
                .assertCause(UnsupportedOperationException.class)
                .assertCauseMessageContains(MESSAGE);
    }

    @Test
    void messageIsValidAndDoesNotContainStringNull_whenEqualsVerifierFails_givenOriginalExceptionIsBare() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(IllegalStateExceptionThrower.class).verify())
                .assertMessageContains(IllegalStateExceptionThrower.class.getSimpleName())
                .assertMessageContains(SEE_ALSO, WEBSITE_URL, "<no message>")
                .assertMessageMatches(SUFFIX)
                .assertMessageDoesNotContainAfterRemove("EqualsVerifier null", "null")
                .assertCause(IllegalStateException.class);
    }

    @Test
    void noStackOverflowErrorIsThrown_whenClassIsARecursiveDatastructure() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(Node.class).verify())
                .assertMessageContains(Node.class.getSimpleName(), SEE_ALSO, WEBSITE_URL)
                .assertMessageMatches(SUFFIX)
                .assertNotCause(StackOverflowError.class);
    }

    @SuppressWarnings("EqualsHashCode")
    private static final class AssertionExceptionWithCauseThrower {

        @Override
        public boolean equals(Object obj) {
            Throwable cause = new NullPointerException();
            throw new AssertionException(Formatter.of(MESSAGE), cause);
        }
    }

    @SuppressWarnings("EqualsHashCode")
    private static final class UnsupportedOperationExceptionWithMessageThrower {

        @Override
        public boolean equals(Object obj) {
            throw new UnsupportedOperationException(MESSAGE);
        }
    }

    @SuppressWarnings("EqualsHashCode")
    private static final class IllegalStateExceptionThrower {

        @Override
        public boolean equals(Object obj) {
            throw new IllegalStateException();
        }
    }
}
