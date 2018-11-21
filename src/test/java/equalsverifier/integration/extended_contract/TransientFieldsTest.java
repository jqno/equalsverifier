package equalsverifier.integration.extended_contract;

import equalsverifier.EqualsVerifier;
import equalsverifier.utils.Warning;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.annotations.javax.persistence.Transient;
import org.junit.Test;

import java.util.Objects;

public class TransientFieldsTest extends ExpectedExceptionTestBase {
    @Test
    public void succeed_whenFieldsWithTransientModifierAreNotUsedInEquals() {
        EqualsVerifier.forClass(NotUsingFieldsWithTransientModifier.class)
                .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientModifierAreUsedInEquals() {
        expectFailure("Transient field", "should not be included in equals/hashCode contract");
        EqualsVerifier.forClass(UsingFieldsWithTransientModifier.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldsWithTransientModifierAreUsedInEquals_givenWarningsAreSuppressed() {
        EqualsVerifier.forClass(UsingFieldsWithTransientModifier.class)
                .suppress(Warning.TRANSIENT_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientModifierAreUsedInEquals_givenTheyreDeclaredInSuperclass() {
        expectFailure("Transient field", "should not be included in equals/hashCode contract");
        EqualsVerifier.forClass(SubclassUsingFieldsWithTransientModifier.class)
            .verify();
    }

    @Test
    public void succeed_whenFieldsWithTransientAnnotationAreNotUsedInEquals() {
        EqualsVerifier.forClass(NotUsingFieldsWithTransientAnnotation.class)
            .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientAnnotationAreUsedInEquals() {
        expectFailure("Transient field", "should not be included in equals/hashCode contract");
        EqualsVerifier.forClass(UsingFieldsWithTransientAnnotation.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldsWithTransientAnnotationAreUsedInEquals_givenWarningsAreSuppressed() {
        EqualsVerifier.forClass(UsingFieldsWithTransientAnnotation.class)
                .suppress(Warning.TRANSIENT_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientAnnotationAreUsedInEquals_givenTheyreDeclaredInSuperclass() {
        expectFailure("Transient field", "should not be included in equals/hashCode contract");
        EqualsVerifier.forClass(SubclassUsingFieldsWithTransientAnnotation.class)
            .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientAnnotationAreNotUsedInEquals_givenAnnotationIsNotAJpaAnnotation() {
        expectFailure("Significant fields", "equals does not use j, or it is stateless");
        EqualsVerifier.forClass(NotUsingFieldsWithNonJpaTransientAnnotation.class)
                .verify();
    }

    static class NotUsingFieldsWithTransientModifier {
        private final int i;
        private final transient int j;

        public NotUsingFieldsWithTransientModifier(int i, int j) { this.i = i; this.j = j; }

        @Override public final boolean equals(Object obj) {
            if (!(obj instanceof NotUsingFieldsWithTransientModifier)) {
                return false;
            }
            NotUsingFieldsWithTransientModifier other = (NotUsingFieldsWithTransientModifier)obj;
            return i == other.i;
        }

        @Override public final int hashCode() { return i; }
    }

    static class UsingFieldsWithTransientModifier {
        private final int i;
        private final transient int j;

        public UsingFieldsWithTransientModifier(int i, int j) { this.i = i; this.j = j; }

        @Override public final boolean equals(Object obj) {
            if (!(obj instanceof UsingFieldsWithTransientModifier)) {
                return false;
            }
            UsingFieldsWithTransientModifier other = (UsingFieldsWithTransientModifier)obj;
            return i == other.i && j == other.j;
        }

        @Override public final int hashCode() { return Objects.hash(i, j); }
    }

    static class SubclassUsingFieldsWithTransientModifier extends UsingFieldsWithTransientModifier {
        public SubclassUsingFieldsWithTransientModifier(int i, int j) { super(i, j); }
    }

    static class NotUsingFieldsWithTransientAnnotation {
        private final int i;
        @Transient private final int j;

        public NotUsingFieldsWithTransientAnnotation(int i, int j) { this.i = i; this.j = j; }

        @Override public final boolean equals(Object obj) {
            if (!(obj instanceof NotUsingFieldsWithTransientAnnotation)) {
                return false;
            }
            NotUsingFieldsWithTransientAnnotation other = (NotUsingFieldsWithTransientAnnotation)obj;
            return i == other.i;
        }

        @Override public final int hashCode() { return i; }
    }

    static class UsingFieldsWithTransientAnnotation {
        private final int i;
        @Transient private final int j;

        public UsingFieldsWithTransientAnnotation(int i, int j) { this.i = i; this.j = j; }

        @Override public final boolean equals(Object obj) {
            if (!(obj instanceof UsingFieldsWithTransientAnnotation)) {
                return false;
            }
            UsingFieldsWithTransientAnnotation other = (UsingFieldsWithTransientAnnotation)obj;
            return i == other.i && j == other.j;
        }

        @Override public final int hashCode() { return Objects.hash(i, j); }
    }

    static class SubclassUsingFieldsWithTransientAnnotation extends UsingFieldsWithTransientAnnotation {
        public SubclassUsingFieldsWithTransientAnnotation(int i, int j) { super(i, j); }
    }

    static class NotUsingFieldsWithNonJpaTransientAnnotation {
        private final int i;
        @equalsverifier.testhelpers.annotations.Transient private final int j;

        public NotUsingFieldsWithNonJpaTransientAnnotation(int i, int j) { this.i = i; this.j = j; }

        @Override public final boolean equals(Object obj) {
            if (!(obj instanceof NotUsingFieldsWithNonJpaTransientAnnotation)) {
                return false;
            }
            NotUsingFieldsWithNonJpaTransientAnnotation other = (NotUsingFieldsWithNonJpaTransientAnnotation)obj;
            return i == other.i;
        }

        @Override public final int hashCode() { return i; }
    }
}
