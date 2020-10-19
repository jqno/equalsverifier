package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;
import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;

import java.io.IOException;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.util.Rethrow.ThrowingRunnable;
import nl.jqno.equalsverifier.internal.util.Rethrow.ThrowingSupplier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

public class RethrowTest extends ExpectedExceptionTestBase {
    private static final String PROVIDED_MSG = "this message is provided";
    private static final String CAUSE_MSG = "this message came from the exception";

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(Rethrow.class);
    }

    @Test
    public void supplierWithIgnoredMessageThrowsRuntimeException() {
        expectException(RuntimeException.class, CAUSE_MSG);
        rethrow(supply(new RuntimeException(CAUSE_MSG)), PROVIDED_MSG);
    }

    @Test
    public void supplierWithMessageThrowsReflectiveOperationException() {
        expectException(ReflectionException.class, PROVIDED_MSG);
        rethrow(supply(new IllegalAccessException(CAUSE_MSG)), PROVIDED_MSG);
    }

    @Test
    public void supplierWithMessageThrowsException() {
        expectException(EqualsVerifierInternalBugException.class, PROVIDED_MSG);
        rethrow(supply(new IOException(CAUSE_MSG)), PROVIDED_MSG);
    }

    @Test
    public void supplierWithNoMessageThrowsRuntimeException() {
        expectException(RuntimeException.class, CAUSE_MSG);
        rethrow(supply(new RuntimeException(CAUSE_MSG)));
    }

    @Test
    public void supplierWithNoMessageThrowsReflectiveOperationException() {
        expectException(ReflectionException.class, CAUSE_MSG);
        rethrow(supply(new IllegalAccessException(CAUSE_MSG)));
    }

    @Test
    public void supplierWithNoMessageThrowsException() {
        expectException(EqualsVerifierInternalBugException.class, CAUSE_MSG);
        rethrow(supply(new IOException(CAUSE_MSG)));
    }

    @Test
    public void runnableWithIgnoredMessageThrowsRuntimeException() {
        expectException(RuntimeException.class, CAUSE_MSG);
        rethrow(run(new RuntimeException(CAUSE_MSG)), PROVIDED_MSG);
    }

    @Test
    public void runnableWithMessageThrowsReflectiveOperationException() {
        expectException(ReflectionException.class, PROVIDED_MSG);
        rethrow(run(new IllegalAccessException(CAUSE_MSG)), PROVIDED_MSG);
    }

    @Test
    public void runnableWithMessageThrowsException() {
        expectException(EqualsVerifierInternalBugException.class, PROVIDED_MSG);
        rethrow(run(new IOException(CAUSE_MSG)), PROVIDED_MSG);
    }

    @Test
    public void runnableWithNoMessageThrowsRuntimeException() {
        expectException(RuntimeException.class, CAUSE_MSG);
        rethrow(run(new RuntimeException(CAUSE_MSG)));
    }

    @Test
    public void runnableWithNoMessageThrowsReflectiveOperationException() {
        expectException(ReflectionException.class, CAUSE_MSG);
        rethrow(run(new IllegalAccessException(CAUSE_MSG)));
    }

    @Test
    public void runnableWithNoMessageThrowsException() {
        expectException(EqualsVerifierInternalBugException.class, CAUSE_MSG);
        rethrow(run(new IOException(CAUSE_MSG)));
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
