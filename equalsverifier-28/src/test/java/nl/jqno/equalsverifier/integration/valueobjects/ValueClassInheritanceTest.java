package nl.jqno.equalsverifier.integration.valueobjects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class ValueClassInheritanceTest {

    @Test
    void succeed_whenValueClassExtendsAbstractValueClass() {
        EqualsVerifier.forClass(ExtendsEmptyAbstractValue.class).verify();
    }

    @Test
    void succeed_whenValueClassExtendsAbstractValueClassWithFields() {
        EqualsVerifier.forClass(ExtendsAbstractValueWithField.class).verify();
    }

    @Test
    void succeed_whenValueClassExtendsSealedAbstractValueClass() {
        EqualsVerifier.forClass(ExtendsSealedAbstractValue.class).verify();
    }

    @Test
    void succeed_whenValueClassImplementsInterface() {
        EqualsVerifier.forClass(ImplementsInterfaceValue.class).verify();
    }

    @Test
    void fail_whenValueClassInHierarchyIgnoresSuperclassField() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(IgnoresSuperclassFieldValue.class).verify())
                .isInstanceOf(AssertionError.class);
    }

    static abstract value class EmptyAbstractValue {}

    static value class ExtendsEmptyAbstractValue extends EmptyAbstractValue {
        private final int i;

        public ExtendsEmptyAbstractValue(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ExtendsEmptyAbstractValue other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(i);
        }
    }

    static abstract value class AbstractValueWithField {
        private final String s;

        public AbstractValueWithField(String s) {
            this.s = s;
        }

        public String s() {
            return s;
        }
    }

    static value class ExtendsAbstractValueWithField extends AbstractValueWithField {
        private final String t;

        public ExtendsAbstractValueWithField(String s, String t) {
            this.t = t;
            super(s);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ExtendsAbstractValueWithField other
                    && Objects.equals(s(), other.s())
                    && Objects.equals(t, other.t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s(), t);
        }
    }

    sealed static abstract value class SealedAbstractValue permits ExtendsSealedAbstractValue {}

    static value class ExtendsSealedAbstractValue extends SealedAbstractValue {
        private final String s;

        public ExtendsSealedAbstractValue(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ExtendsSealedAbstractValue other && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(s);
        }
    }

    interface SomeInterface {
        String s();
    }

    static value class ImplementsInterfaceValue implements SomeInterface {
        private final String s;

        public ImplementsInterfaceValue(String s) {
            this.s = s;
        }

        @Override
        public String s() {
            return s;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ImplementsInterfaceValue other && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(s);
        }
    }

    static value class IgnoresSuperclassFieldValue extends AbstractValueWithField {
        private final String t;

        public IgnoresSuperclassFieldValue(String s, String t) {
            this.t = t;
            super(s);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IgnoresSuperclassFieldValue other && Objects.equals(t, other.t);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(t);
        }
    }
}
