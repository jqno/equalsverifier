package nl.jqno.equalsverifier.internal.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class EqualsVerifierInternalBugExceptionTest {
    private EqualsVerifierInternalBugException actual;

    @Test
    public void exceptionHasNoSpecificMessage() {
        actual = new EqualsVerifierInternalBugException();

        assertNoMessage();
        assertNoCause();
    }

    @Test
    public void exceptionHasSpecificMessage() {
        String message = "my message";
        actual = new EqualsVerifierInternalBugException(message);

        assertMessage(message);
        assertNoCause();
    }

    @Test
    public void exceptionHasACause() {
        Throwable cause = new IllegalStateException("cause of the bug");
        actual = new EqualsVerifierInternalBugException(cause);

        assertNoMessage();
        assertCause(cause);
    }

    @Test
    public void exceptionHasMessageAndCause() {
        String message = "some message";
        Throwable cause = new IllegalArgumentException("some cause");
        actual = new EqualsVerifierInternalBugException(message, cause);

        assertMessage(message);
        assertCause(cause);
    }

    private void assertNoMessage() {
        assertMessagePreamble();
        assertThat(actual.getMessage(), not(containsString("\n")));
    }

    private void assertMessage(String message) {
        assertMessagePreamble();
        assertThat(actual.getMessage(), containsString("\n"));
        assertThat(actual.getMessage(), containsString(message));
    }

    private void assertMessagePreamble() {
        assertThat(actual.getMessage(), containsString("This is a bug in EqualsVerifier"));
    }

    private void assertNoCause() {
        assertThat(actual.getCause(), is(nullValue()));
    }

    private void assertCause(Throwable cause) {
        assertThat(actual.getCause(), is(cause));
    }
}
