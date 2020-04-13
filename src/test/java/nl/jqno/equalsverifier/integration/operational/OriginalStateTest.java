package nl.jqno.equalsverifier.integration.operational;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import org.junit.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class OriginalStateTest extends ExpectedExceptionTestBase {
    private static final String INSTANCE_1 = "instance 1";
    private static final String INSTANCE_2 = "instance 2";
    private static final String STATIC = "static";
    private static final String STATIC_FINAL = "static final";

    @Test
    public void staticValueReturnsToOriginalState_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(CorrectEquals.class).verify();
        assertEquals(STATIC_FINAL, CorrectEquals.STATIC_FINAL_VALUE);
        assertEquals(STATIC, CorrectEquals.staticValue);
    }

    @Test
    public void staticValueReturnsToOriginalStateRecursively_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(CorrectEqualsContainer.class).verify();
        assertEquals(STATIC, CorrectEquals.staticValue);
    }

    @Test
    public void staticValueReturnsToOriginalStateDeeplyRecursively_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(CorrectEqualsContainerContainer.class).verify();
        assertEquals(STATIC, CorrectEquals.staticValue);
    }

    @Test
    public void staticValueInSuperReturnsToOriginalState_whenEqualsVerifierIsFinished() {
        EqualsVerifier.forClass(SubContainer.class).verify();
        assertEquals(STATIC, SuperContainer.staticValue);
        assertEquals(STATIC_FINAL, SuperContainer.STATIC_FINAL_VALUE);
    }

    @Test
    public void allValuesReturnToOriginalState_whenEqualsVerifierIsFinishedWithException() {
        try {
            EqualsVerifier.forClass(FailingEqualsContainerContainer.class).verify();
            fail("EqualsVerifier should have failed on FailingEqualsContainerContainer.");
        } catch (AssertionError e) {
            // Make sure EV fails on a check that actually mutates fields.
            assertTrue(e.getMessage().contains("Mutability"));
        } catch (Throwable ignored) {
            fail(
                    "EqualsVerifier should have failed on FailingEqualsContainerContainer with a different exception.");
        }

        assertEquals(STATIC_FINAL, CorrectEquals.STATIC_FINAL_VALUE);
        assertEquals(STATIC, CorrectEquals.staticValue);
    }

    static final class CorrectEquals {
        private static final String STATIC_FINAL_VALUE = STATIC_FINAL;
        private static String staticValue = STATIC;
        private final String instanceValue;

        public CorrectEquals(String instanceValue) {
            this.instanceValue = instanceValue;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class CorrectEqualsContainer {
        private final CorrectEquals foo;

        public CorrectEqualsContainer(CorrectEquals foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class CorrectEqualsContainerContainer {
        private final CorrectEqualsContainer foo;

        public CorrectEqualsContainerContainer(CorrectEqualsContainer foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    abstract static class SuperContainer {
        private static final String STATIC_FINAL_VALUE = STATIC_FINAL;
        private static String staticValue = STATIC;

        private final CorrectEquals foo;

        public SuperContainer(CorrectEquals foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SuperContainer)) {
                return false;
            }
            SuperContainer other = (SuperContainer) obj;
            return Objects.equals(foo, other.foo);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class SubContainer extends SuperContainer {
        public SubContainer(CorrectEquals foo) {
            super(foo);
        }
    }

    static final class FailingEqualsContainerContainer {
        private CorrectEqualsContainer foo;

        public FailingEqualsContainerContainer(CorrectEqualsContainer foo) {
            this.foo = foo;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
