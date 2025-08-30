package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CheckReturnValue")
public class WithResettablePrefabValueTest {
    @Test
    void succeed_whenRecordHasMutableBackReference() {
        EqualsVerifier
                .forClass(WithBackReference.class)
                .withResettablePrefabValue(
                    BackReferenceContainer.class,
                    () -> new BackReferenceContainer(1),
                    () -> new BackReferenceContainer(2))
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    void fail_whenTypeIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(null, () -> new Object(), () -> new Object()))
                .assertThrows(NullPointerException.class)
                .assertMessageContains("Precondition:", "prefab value type is null");
    }

    @Test
    void fail_whenRedSupplierIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, null, () -> new Object()))
                .assertThrows(NullPointerException.class)
                .assertMessageContains("Precondition:", "red supplier is null");
    }

    @Test
    void fail_whenBlueSupplierIsNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, () -> new Object(), null))
                .assertThrows(NullPointerException.class)
                .assertMessageContains("Precondition:", "blue supplier is null");
    }

    @Test
    void fail_whenRedValueSuppliesNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, () -> null, () -> new Object()))
                .assertThrows(NullPointerException.class)
                .assertMessageContains("Precondition:", "red prefab value", "is null");
    }

    @Test
    void fail_whenBlueValueSuppliesNull() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, () -> new Object(), () -> null))
                .assertThrows(NullPointerException.class)
                .assertMessageContains("Precondition:", "blue prefab value", "is null");
    }

    @Test
    void fail_whenBothValuesAreEqual() {
        var constant = new Object();
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, () -> constant, () -> constant))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition:", "both prefab values", "are equal");
    }

    @Test
    void fail_whenRedValueIsntEqualToItself() {
        var constant = new Object();
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, () -> new Object(), () -> constant))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition:", "red prefab value is not equal to itself after reset");
    }

    @Test
    void fail_whenBlueValueIsntEqualToItself() {
        var constant = new Object();
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(Point.class)
                            .withResettablePrefabValue(Object.class, () -> constant, () -> new Object()))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition:", "blue prefab value is not equal to itself after reset");
    }

    public record WithBackReference(BackReferenceContainer state) {

        public WithBackReference {
            state.setBackReference(this);
        }
    }

    public static final class BackReferenceContainer {
        private final int i;
        private Object backReference;

        public BackReferenceContainer(int i) {
            this.i = i;
        }

        public void setBackReference(Object newBackReference) {
            if (this.backReference != null) {
                throw new IllegalStateException("It's already set!");
            }
            this.backReference = newBackReference;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BackReferenceContainer other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

}
