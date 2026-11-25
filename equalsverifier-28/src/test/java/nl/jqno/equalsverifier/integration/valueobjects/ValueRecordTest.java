package nl.jqno.equalsverifier.integration.valueobjects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ValueRecordTest {

    @Test
    void succeed_whenValueRecordHasDefaultEquals() {
        // String is an identity class, even under JEP 401.
        EqualsVerifier.forClass(SimpleValueRecord.class).verify();
    }

    @Test
    void succeed_whenValueRecordHasFloatField() {
        // A value record's default equals treats all NaN values as interchangeable,
        // while == distinguishes them by their raw bit patterns.
        EqualsVerifier.forClass(FloatValueRecord.class).verify();
    }

    @Test
    void succeed_whenValueRecordHasDoubleField() {
        EqualsVerifier.forClass(DoubleValueRecord.class).verify();
    }

    @Test
    void succeed_whenValueRecordContainsAnotherValueRecord() {
        EqualsVerifier.forClass(NestedValueRecord.class).verify();
    }

    @Test
    void succeed_whenValueRecordIsDeeplyNested() {
        // Exercises the recursive nature of == and equals for value objects.
        EqualsVerifier.forClass(DeeplyNestedValueRecord.class).verify();
    }

    @Test
    void fail_whenValueRecordEqualsIgnoresField() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(NotAllFieldsValueRecord.class).verify())
                .isInstanceOf(AssertionError.class);
    }

    value record SimpleValueRecord(int i, String s) {}

    value record FloatValueRecord(float f) {}

    value record DoubleValueRecord(double d) {}

    value record NestedValueRecord(FloatValueRecord r) {}

    value record DeeplyNestedValueRecord(NestedValueRecord r) {}

    value record NotAllFieldsValueRecord(int i, int j) {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof NotAllFieldsValueRecord other && i == other.i;
        }
    }
}
