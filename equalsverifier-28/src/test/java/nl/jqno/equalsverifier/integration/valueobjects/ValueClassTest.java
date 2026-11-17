package nl.jqno.equalsverifier.integration.valueobjects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

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
        EqualsVerifier.forClass(NoOverrideValue.class).verify();
    }

    @Test
    void succeed_whenValueClassOverridesEqualsButInheritsHashCode() {
        // For a value class, the inherited Object.hashCode is derived from field
        // values, so it is consistent with a field-based equals even without an
        // explicit hashCode override.
        EqualsVerifier.forClass(EqualsButNoHashCode.class).verify();
    }

    @Test
    void succeed_whenValueClassEqualsDiffersFromReferenceEquality() {
        // Like JEP 401's Substring: two instances can be equals even when their
        // fields differ, because equals is defined in terms of the string the
        // instance represents rather than its internal state.
        EqualsVerifier.forClass(Substring.class).verify();
    }

    @Test
    void succeed_whenValueClassHasFloatField() {
        EqualsVerifier.forClass(Temperature.class).verify();
    }

    @Test
    void succeed_whenValueClassContainsAnotherValueObject() {
        EqualsVerifier.forClass(Sample.class).verify();
    }

    @Test
    void succeed_whenValueClassContainsIdentityObject() {
        EqualsVerifier.forClass(EmailAddress.class).verify();
    }

    @Test
    void succeed_whenValueClassHasMultipleConstructors() {
        EqualsVerifier.forClass(Money.class).verify();
    }

    @Test
    void succeed_whenValueClassHasPrivateMatchingConstructor() {
        EqualsVerifier.forClass(PrivateCtorValue.class).verify();
    }

    @Test
    void fail_whenValueClassEqualsIgnoresField() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(BrokenValue.class).verify())
                .isInstanceOf(AssertionError.class);
    }

    static value

    class SimpleValue {
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
    static value class NoOverrideValue {
        private final int i;
        private final String s;

        public NoOverrideValue(int i, String s) {
            this.i = i;
            this.s = s;
        }
        // inherits Object's value-object equals and hashCode
    }

    @SuppressWarnings({"ValueClassEquals", "EqualsHashCode"})
    static value class EqualsButNoHashCode {
        private final int i;
        private final String s;

        public EqualsButNoHashCode(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EqualsButNoHashCode other && i == other.i && Objects.equals(s, other.s);
        }
        // hashCode inherited from Object's value-object behavior
    }

    static value

    class Substring {
        private final String str;
        private final int start;
        private final int end;

        public Substring(String s, int i, int j) {
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
            return obj instanceof Substring other && toString().equals(other.toString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(Substring.class, toString());
        }
    }

    static value

    class Temperature {
        private final float celsius;

        public Temperature(float celsius) {
            this.celsius = celsius;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Temperature other && Float.compare(celsius, other.celsius) == 0;
        }

        @Override
        public int hashCode() {
            return Float.hashCode(celsius);
        }
    }

    static value

    class Sample {
        private final Temperature temperature;
        private final int count;

        public Sample(Temperature temperature, int count) {
            this.temperature = temperature;
            this.count = count;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Sample other
                    && Objects.equals(temperature, other.temperature)
                    && count == other.count;
        }

        @Override
        public int hashCode() {
            return Objects.hash(temperature, count);
        }
    }

    static value

    class EmailAddress {
        private final String local;
        private final String domain;

        public EmailAddress(String local, String domain) {
            this.local = local;
            this.domain = domain;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EmailAddress other
                    && Objects.equals(local, other.local)
                    && Objects.equals(domain, other.domain);
        }

        @Override
        public int hashCode() {
            return Objects.hash(local, domain);
        }
    }

    static value

    class Money {
        private final long cents;

        public Money(long cents) {
            this.cents = cents;
        }

        public Money(long euros, int cents) {
            this(euros * 100 + cents);
        }

        public Money(double euros) {
            this((long) Math.round(euros * 100));
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Money other && cents == other.cents;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(cents);
        }
    }

    static value

    class PrivateCtorValue {
        private final int i;
        private final String s;

        @SuppressWarnings("unused")
        private PrivateCtorValue(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PrivateCtorValue other && i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    static value

    class BrokenValue {
        private final int i;
        @SuppressWarnings("unused")
        private final String s;

        public BrokenValue(int i, String s) {
            this.i = i;
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BrokenValue other && i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}
