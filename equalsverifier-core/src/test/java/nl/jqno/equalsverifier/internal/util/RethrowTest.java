package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;
import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;

import java.io.IOException;

import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.util.Rethrow.ThrowingRunnable;
import nl.jqno.equalsverifier.internal.util.Rethrow.ThrowingSupplier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class RethrowTest {

    private static final String PROVIDED_MSG = "this message is provided";
    private static final String CAUSE_MSG = "this message came from the exception";

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(Rethrow.class);
    }

    @Test
    void supplierWithIgnoredMessageThrowsRuntimeException() {
        ExpectedException
                .when(() -> rethrow(supply(new RuntimeException(CAUSE_MSG)), e -> PROVIDED_MSG))
                .assertThrows(RuntimeException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void supplierWithMessageThrowsReflectiveOperationException() {
        ExpectedException
                .when(() -> rethrow(supply(new IllegalAccessException(CAUSE_MSG)), e -> PROVIDED_MSG))
                .assertThrows(ReflectionException.class)
                .assertMessageContains(PROVIDED_MSG);
    }

    @Test
    void supplierWithMessageThrowsException() {
        ExpectedException
                .when(() -> rethrow(supply(new IOException(CAUSE_MSG)), e -> PROVIDED_MSG))
                .assertThrows(EqualsVerifierInternalBugException.class)
                .assertMessageContains(PROVIDED_MSG);
    }

    @Test
    void supplierWithNoMessageThrowsRuntimeException() {
        ExpectedException
                .when(() -> rethrow(supply(new RuntimeException(CAUSE_MSG))))
                .assertThrows(RuntimeException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void supplierWithNoMessageThrowsReflectiveOperationException() {
        ExpectedException
                .when(() -> rethrow(supply(new IllegalAccessException(CAUSE_MSG))))
                .assertThrows(ReflectionException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void supplierWithNoMessageThrowsException() {
        ExpectedException
                .when(() -> rethrow(supply(new IOException(CAUSE_MSG))))
                .assertThrows(EqualsVerifierInternalBugException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void runnableWithIgnoredMessageThrowsRuntimeException() {
        ExpectedException
                .when(() -> rethrow(run(new RuntimeException(CAUSE_MSG)), e -> PROVIDED_MSG))
                .assertThrows(RuntimeException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void runnableWithMessageThrowsReflectiveOperationException() {
        ExpectedException
                .when(() -> rethrow(run(new IllegalAccessException(CAUSE_MSG)), e -> PROVIDED_MSG))
                .assertThrows(ReflectionException.class)
                .assertMessageContains(PROVIDED_MSG);
    }

    @Test
    void runnableWithMessageThrowsException() {
        ExpectedException
                .when(() -> rethrow(run(new IOException(CAUSE_MSG)), e -> PROVIDED_MSG))
                .assertThrows(EqualsVerifierInternalBugException.class)
                .assertMessageContains(PROVIDED_MSG);
    }

    @Test
    void runnableWithNoMessageThrowsRuntimeException() {
        ExpectedException
                .when(() -> rethrow(run(new RuntimeException(CAUSE_MSG))))
                .assertThrows(RuntimeException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void runnableWithNoMessageThrowsReflectiveOperationException() {
        ExpectedException
                .when(() -> rethrow(run(new IllegalAccessException(CAUSE_MSG))))
                .assertThrows(ReflectionException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    @Test
    void runnableWithNoMessageThrowsException() {
        ExpectedException
                .when(() -> rethrow(run(new IOException(CAUSE_MSG))))
                .assertThrows(EqualsVerifierInternalBugException.class)
                .assertMessageContains(CAUSE_MSG);
    }

    private ThrowingSupplier<?> supply(Exception e) {
        return () -> {
            throw e;
        };
    }

    private ThrowingRunnable run(Exception e) {
        return () -> {
            throw e;
        };
    }
}
