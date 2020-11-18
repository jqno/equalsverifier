package nl.jqno.equalsverifier.testhelpers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.function.Executable;

public final class ExpectedException {
    private final Throwable e;

    private ExpectedException(Throwable e) {
        this.e = e;
    }

    public static ExpectedException when(Executable executable) {
        try {
            executable.execute();
            return new ExpectedException(null);
        } catch (Throwable e) {
            return new ExpectedException(e);
        }
    }

    public ExpectedException assertThrows(Class<? extends Throwable> expectedException) {
        assertNotNull(e, "Expected " + expectedException.getSimpleName() + " but none was thrown.");
        assertTrue(
                expectedException.isInstance(e),
                "Expected "
                        + expectedException.getCanonicalName()
                        + " but was "
                        + e.getClass().getCanonicalName()
                        + ".");
        return this;
    }

    public ExpectedException assertFailure() {
        return assertThrows(AssertionError.class);
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
}
