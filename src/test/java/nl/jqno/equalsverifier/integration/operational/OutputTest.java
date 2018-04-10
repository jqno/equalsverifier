package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.exceptions.AssertionException;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;
import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.Node;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;

public class OutputTest extends IntegrationTestBase {
    private static final String SEE_ALSO = "For more information, go to";
    private static final String WIKIPAGE_URL = "http://www.jqno.nl/equalsverifier/errormessages";
    private static final String MESSAGE = "a message for an exception";

    private static final String[] BLACKLISTED_EXCEPTIONS = {
            AssertionError.class.getSimpleName(),
            MessagingException.class.getSimpleName(),
            RecursionException.class.getSimpleName(),
            AssertionException.class.getSimpleName(),
            ReflectionException.class.getSimpleName()
    };

    @Test
    public void messageIsValidAndExceptionHasNoCause_whenEqualsVerifierFails_givenExceptionIsGeneratedByEqualsVerifierItself() {
        expectMessageIsValid();
        expectExceptionHasNoCause();

        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    public void messageIsValidAndExceptionHasCause_whenEqualsVerifierFails_givenOriginalExceptionHasACause() {
        expectMessageIsValid();
        expectMessageContains(MESSAGE);
        expectMessageDoesNotContain(NullPointerException.class.getSimpleName());
        expectCause(NullPointerException.class);

        EqualsVerifier.forClass(AssertionExceptionWithCauseThrower.class).verify();
    }

    @Test
    public void originalMessageIsPresentInOutput_whenEqualsVerifierFails_givenOriginalExceptionHasAMessage() {
        expectMessageIsValid();
        expectMessageContains(UnsupportedOperationException.class.getSimpleName(), MESSAGE);
        expectMessageDoesNotContain("null");
        expectCause(UnsupportedOperationException.class, MESSAGE);

        EqualsVerifier.forClass(UnsupportedOperationExceptionWithMessageThrower.class).verify();
    }

    @Test
    public void messageIsValidAndDoesNotContainStringNull_whenEqualsVerifierFails_givenOriginalExceptionIsBare() {
        expectMessageIsValid();
        expectMessageContains(IllegalStateException.class.getSimpleName());
        expectMessageDoesNotContain("null");
        expectCause(IllegalStateException.class);

        EqualsVerifier.forClass(IllegalStateExceptionThrower.class).verify();
    }

    @Test
    public void noStackOverflowErrorIsThrown_whenClassIsARecursiveDatastructure() {
        expectMessageIsValid();
        expectExceptionHasNoCause();

        EqualsVerifier.forClass(Node.class).verify();
    }

    private void expectMessageIsValid() {
        expectMessageContains(SEE_ALSO, WIKIPAGE_URL);
        expectMessageDoesNotContain(BLACKLISTED_EXCEPTIONS);
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

    private void expectExceptionHasNoCause() {
        expectFailureWithCause(null);
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
