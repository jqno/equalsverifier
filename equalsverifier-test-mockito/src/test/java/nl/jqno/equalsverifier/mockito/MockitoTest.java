package nl.jqno.equalsverifier.mockito;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.MockitoException;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.FinalPointContainer;
import nl.jqno.equalsverifier_testhelpers.types.PointContainer;
import nl.jqno.equalsverifier_testhelpers.types.PreconditionTypeHelper;
import nl.jqno.equalsverifier_testhelpers.types.SparseArrays.SparseArrayDirectEqualsContainer;
import nl.jqno.equalsverifier_testhelpers.types.SparseArrays.SparseArrayEqualsContainer;
import org.junit.jupiter.api.Test;

public class MockitoTest {
    @Test
    void verifySimpleClass() {
        EqualsVerifier.forClass(PointContainer.class).verify();
    }

    @Test
    void verifyFinalClassContainer() {
        EqualsVerifier.forClass(FinalPointContainer.class).verify();
    }

    @Test
    void verifyRecordWithPrecondition() {
        EqualsVerifier.forClass(SinglePreconditionRecordContainer.class).verify();
    }

    @Test
    void verifyClassThatCallsDirectlyIntoField() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(PojoWithoutEqualsContainer.class)
                            .suppress(Warning.NULL_FIELDS)
                            .verify())
                .assertFailure()
                .assertCause(MockitoException.class)
                .assertMessageContains("Unable to use Mockito to mock field methodCaller of type PojoWithoutEquals.");
    }

    @Test
    void verifyClassThatCallsDirectlyIntoField_givenPrefabValue() {
        var red = new PojoWithoutEquals(1);
        var blue = new PojoWithoutEquals(2);
        EqualsVerifier
                .forClass(PojoWithoutEqualsContainer.class)
                .withPrefabValues(PojoWithoutEquals.class, red, blue)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    void verifyClassWithFieldWhoseSuperOverridesEquals() {
        EqualsVerifier.forClass(SubContainerThatAlsoHasARecursion.class).verify();
    }

    @Test
    void verifyClassThatContainsSparseArrayThatCallsMethods() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SparseArrayEqualsContainer.class).verify())
                .assertFailure()
                .assertCause(MockitoException.class)
                .assertMessageContains("Unable to use Mockito to mock field sparseArray of type SparseArray");
    }

    @Test
    void verifyClassThatContainsSparseArrayThatCallsDirectlyIntoFields() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SparseArrayDirectEqualsContainer.class).verify())
                .assertFailure()
                // Because Mockito will generate a sparseArray with empty list of items
                .assertMessageContains("Significant fields: equals does not use sparseArray, or it is stateless");
    }

    record SinglePreconditionRecordContainer(PreconditionTypeHelper.SinglePreconditionRecord r) {}

    static final class PojoWithoutEquals {
        private final int i;

        PojoWithoutEquals(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }
    }

    static final class PojoWithoutEqualsContainer {
        private final PojoWithoutEquals methodCaller;

        PojoWithoutEqualsContainer(PojoWithoutEquals methodCaller) {
            this.methodCaller = methodCaller;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PojoWithoutEqualsContainer other && methodCaller.getI() == other.methodCaller.getI();
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodCaller.getI());
        }
    }

    static class Super {
        private final int i;

        Super(int i) {
            this.i = i;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof Super other && i == other.i;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class Sub extends Super {
        Sub(int i) {
            super(i);
        }
    }

    static final class SubContainerThatAlsoHasARecursion {
        private final Sub sub;
        private final SubContainerThatAlsoHasARecursion container;

        SubContainerThatAlsoHasARecursion(Sub sub, SubContainerThatAlsoHasARecursion container) {
            this.sub = sub;
            this.container = container;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SubContainerThatAlsoHasARecursion other
                    && Objects.equals(sub, other.sub)
                    && Objects.equals(container, other.container);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sub, container);
        }
    }
}
