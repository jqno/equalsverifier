package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/**
 * Utilities for catching checked exceptions and re-throwing them as a RuntimeException.
 *
 * <p>Java's reflection API declares lots of checked exceptions that it rarely actually throws, if
 * ever. We still need to catch all of them, but the code in the catch blocks is never executed. It
 * is hard to create tests to do so. Besides, EqualsVerifier contains a lot of extra checks to make
 * sure these cases can never occur.
 *
 * <p>This adds a lot of noise to the test coverage and mutation coverage reports, that needs to be
 * inspected manually every time. This makes me too lazy to actually do it, which is how needless
 * gaps in coverage accumulate. By using Rethrow, we can easily cover the exception code, making
 * gaps in coverage actual gaps in coverage that need to be dealt with instead of ignored over and
 * over.
 *
 * <p>As a nice side effect, it makes it easier to increase the coverage threshold even more, though
 * this is not the purpose of this class.
 */
public final class Rethrow {
    /** Do not instantiate. */
    private Rethrow() {}

    public static <T> T rethrow(ThrowingSupplier<T> supplier, String errorMessage) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            throw new ReflectionException(msg(errorMessage, e), e);
        } catch (Exception e) {
            throw new EqualsVerifierInternalBugException(msg(errorMessage, e), e);
        }
    }

    public static <T> T rethrow(ThrowingSupplier<T> supplier) {
        return rethrow(supplier, null);
    }

    public static void rethrow(ThrowingRunnable block, String errorMessage) {
        rethrow(
                () -> {
                    block.run();
                    return null;
                },
                errorMessage);
    }

    public static void rethrow(ThrowingRunnable block) {
        rethrow(block, null);
    }

    private static String msg(String errorMessage, Throwable e) {
        return errorMessage != null ? errorMessage : e.getMessage();
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
