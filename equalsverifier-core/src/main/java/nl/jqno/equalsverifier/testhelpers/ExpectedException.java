package nl.jqno.equalsverifier.testhelpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.MessagingException;

@SuppressFBWarnings(
    value = "NM_CLASS_NOT_EXCEPTION",
    justification = "Only called in test code, not production."
)
public final class ExpectedException {

    private final Throwable e;

    private ExpectedException(Throwable e) {
        this.e = e;
    }

    public static ExpectedException when(Runnable runnable) {
        try {
            runnable.run();
            return new ExpectedException(null);
        } catch (Throwable e) {
            return new ExpectedException(e);
        }
    }

    public ExpectedException assertThrows(Class<? extends Throwable> expectedException) {
        if (e == null) {
            fail("Expected " + expectedException.getSimpleName() + " but none was thrown.");
        }
        if (!expectedException.isInstance(e)) {
            fail(
                "Expected " +
                expectedException.getCanonicalName() +
                " but was " +
                e.getClass().getCanonicalName() +
                "."
            );
        }
        return this;
    }

    public ExpectedException assertFailure() {
        return assertThrows(AssertionError.class);
    }

    public ExpectedException assertCause(Class<? extends Throwable> expectedCause) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause.getClass().equals(expectedCause)) {
                return this;
            }
            cause = cause.getCause();
        }
        fail("Expected " + expectedCause.getSimpleName() + " to be in the cause but it wasn't.");
        return this;
    }

    public ExpectedException assertNotCause(Class<? extends Throwable> expectedCause) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause.getClass().equals(expectedCause)) {
                fail(
                    "Expected " +
                    expectedCause.getSimpleName() +
                    " not to be in the cause but it was."
                );
            }
            cause = cause.getCause();
        }
        return this;
    }

    public ExpectedException assertCauseMessageContains(String fragment) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause.getMessage() != null && cause.getMessage().contains(fragment)) {
                return this;
            }
            cause = cause.getCause();
        }
        fail("Expected cause to contain " + fragment + " but it doesn't.");
        return this;
    }

    public ExpectedException assertCauseMessageDoesNotContain(String fragment) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause.getMessage() != null && cause.getMessage().contains(fragment)) {
                fail("Expected cause not to contain " + fragment + " but it does.");
            }
            cause = cause.getCause();
        }
        return this;
    }

    public ExpectedException assertMessageContains(String... fragments) {
        String message = e.getMessage();
        for (String fragment : fragments) {
            if (!message.contains(fragment)) {
                fail("Message [" + message + "] does not contain [" + fragment + "]");
            }
        }
        return this;
    }

    public ExpectedException assertMessageDoesNotContain(String... fragments) {
        String message = e.getMessage();
        for (String fragment : fragments) {
            if (message.contains(fragment)) {
                fail("Message [" + message + "] contains [" + fragment + "]");
            }
        }
        return this;
    }

    public ExpectedException assertDescriptionContains(String... fragments) {
        if (!(e instanceof MessagingException)) {
            fail("Exception " + e.getClass().getSimpleName() + " is not a MessagingException.");
        }
        MessagingException me = (MessagingException) e;
        String description = me.getDescription();
        for (String fragment : fragments) {
            if (!description.contains(fragment)) {
                fail("Description [" + description + "] does not contain [" + fragment + "]");
            }
        }
        return this;
    }

    public ExpectedException assertDescriptionDoesNotContain(String... fragments) {
        if (!(e instanceof MessagingException)) {
            fail("Exception " + e.getClass().getSimpleName() + " is not a MessagingException.");
        }
        MessagingException me = (MessagingException) e;
        String description = me.getDescription();
        for (String fragment : fragments) {
            if (description.contains(fragment)) {
                fail("Description [" + description + "] contains [" + fragment + "]");
            }
        }
        return this;
    }

    private void fail(String msg) {
        throw new AssertionError(msg);
    }
}
