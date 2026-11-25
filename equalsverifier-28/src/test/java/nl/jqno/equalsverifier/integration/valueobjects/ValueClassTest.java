package nl.jqno.equalsverifier.integration.valueobjects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ValueClassTest {

    @Test
    void succeed_whenValueClassHasConstructorMatchingFields() {
        EqualsVerifier.forClass(SimpleValue.class).verify();
    }

    @Test
    void succeed_whenValueClassUsesInheritedObjectEquals() {
        // A value class that does not override equals/hashCode inherits Object's
        // value-object behavior: equals compares by field values (==), and
        // hashCode is derived from field values. This is a consistent contract.
        EqualsVerifier.forClass(InheritsObjectEqualsValue.class).verify();
    }

    @Test
    void succeed_whenValueClassOverridesEqualsButInheritsHashCode() {
        // For a value class, the inherited Object.hashCode is derived from field
        // values, so it is consistent with a field-based equals even without an
        // explicit hashCode override.
        EqualsVerifier.forClass(InheritsObjectHashCodeValue.class).verify();
    }

    @Test
    void succeed_whenValueClassEqualsDiffersFromReferenceEquality() {
        // Like JEP 401's Substring: two instances can be equals even when their
        // fields differ, because equals is defined in terms of the string the
        // instance represents rather than its internal state.
        EqualsVerifier.forClass(DerivedStateEqualsValue.class).verify();
    }

    @Test
    void succeed_whenValueClassHasFloatField() {
        EqualsVerifier.forClass(FloatFieldValue.class).verify();
    }

    @Test
    void succeed_whenValueClassContainsAnotherValueObject() {
        EqualsVerifier.forClass(ValueObjectFieldValue.class).verify();
    }

    @Test
    void succeed_whenValueClassHasMultipleConstructors() {
        EqualsVerifier.forClass(MultipleConstructorsValue.class).verify();
    }

    @Test
    void succeed_whenValueClassHasPrivateMatchingConstructor() {
        EqualsVerifier.forClass(PrivateConstructorValue.class).verify();
    }

    @Test
    void fail_whenValueClassEqualsIgnoresField() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(NotAllFieldsValue.class).verify())
                .isInstanceOf(AssertionError.class);
    }

    static value class SimpleValue {
        private final int i;
        private final String s;

        public SimpleValue(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SimpleValue other && i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    @SuppressWarnings({"unused", "ValueClassEquals"})
    static value class InheritsObjectEqualsValue {
        private final int i;
        private final String s;

        public InheritsObjectEqualsValue(int i, String s) {
            this.i = i;
            this.s = s;
        }
        // inherits Object's value-object equals and hashCode
    }

    @SuppressWarnings({"ValueClassEquals", "EqualsHashCode"})
    static value class InheritsObjectHashCodeValue {
        private final int i;
        private final String s;

        public InheritsObjectHashCodeValue(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof InheritsObjectHashCodeValue other && i == other.i && Objects.equals(s, other.s);
        }
        // hashCode inherited from Object's value-object behavior
    }

    static value class DerivedStateEqualsValue {
        private final String str;
        private final int start;
        private final int end;

        public DerivedStateEqualsValue(String s, int i, int j) {
            this.str = s;
            this.start = i;
            this.end = j;
        }

        @Override
        public String toString() {
            return str.substring(start, end);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DerivedStateEqualsValue other && toString().equals(other.toString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(DerivedStateEqualsValue.class, toString());
        }
    }

    static value class FloatFieldValue {
        private final float f;

        public FloatFieldValue(float f) {
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FloatFieldValue other && Float.compare(f, other.f) == 0;
        }

        @Override
        public int hashCode() {
            return Float.hashCode(f);
        }
    }

    static value class ValueObjectFieldValue {
        private final FloatFieldValue value;
        private final int i;

        public ValueObjectFieldValue(FloatFieldValue value, int i) {
            this.value = value;
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValueObjectFieldValue other && Objects.equals(value, other.value) && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, i);
        }
    }

    static value class MultipleConstructorsValue {
        private final long l;

        public MultipleConstructorsValue(long l) {
            this.l = l;
        }

        public MultipleConstructorsValue(long l, int i) {
            this(l * 100 + i);
        }

        public MultipleConstructorsValue(double d) {
            this((long) Math.round(d * 100));
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MultipleConstructorsValue other && l == other.l;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(l);
        }
    }

    static value class PrivateConstructorValue {
        private final int i;
        private final String s;

        @SuppressWarnings("unused")
        private PrivateConstructorValue(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PrivateConstructorValue other && i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    static value class NotAllFieldsValue {
        private final int i;
        @SuppressWarnings("unused")
        private final String s;

        public NotAllFieldsValue(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NotAllFieldsValue other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}
