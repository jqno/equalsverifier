package nl.jqno.equalsverifier.integration.valueobjects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ValueRecordTest {

    @Test
    void succeed_whenValueRecordHasDefaultEquals() {
        EqualsVerifier.forClass(Point.class).verify();
    }

    @Test
    void succeed_whenValueRecordHasSingleStringField() {
        // String is an identity class, even under JEP 401.
        EqualsVerifier.forClass(Name.class).verify();
    }

    @Test
    void succeed_whenValueRecordHasFloatField() {
        // A value record's default equals treats all NaN values as interchangeable,
        // while == distinguishes them by their raw bit patterns.
        EqualsVerifier.forClass(Length.class).verify();
    }

    @Test
    void succeed_whenValueRecordHasDoubleField() {
        EqualsVerifier.forClass(Area.class).verify();
    }

    @Test
    void succeed_whenValueRecordContainsAnotherValueRecord() {
        EqualsVerifier.forClass(Measurement.class).verify();
    }

    @Test
    void succeed_whenValueRecordIsDeeplyNested() {
        // Exercises the recursive nature of == and equals for value objects.
        EqualsVerifier.forClass(DeepBox.class).verify();
    }

    @Test
    void succeed_whenValueRecordContainsIdentityObject() {
        EqualsVerifier.forClass(Labeled.class).verify();
    }

    @Test
    void fail_whenValueRecordEqualsIgnoresField() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(BrokenPoint.class).verify())
                .isInstanceOf(AssertionError.class);
    }

    value record Point(int x, int y) {}

    value record Name(String name) {}

    value record Length(float val) {}

    value record Area(double val) {}

    value record Measurement(Length length) {}

    value record Box(Length length) {}

    value record DeepBox(Box box) {}

    value record Labeled(String label, int index) {}

    value record BrokenPoint(int x, int y) {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof BrokenPoint other && x == other.x;
        }
    }
}
