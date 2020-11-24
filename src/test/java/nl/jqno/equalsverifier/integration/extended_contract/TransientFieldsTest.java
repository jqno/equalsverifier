package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Transient;
import org.junit.jupiter.api.Test;

public class TransientFieldsTest {

    @Test
    public void succeed_whenFieldsWithTransientModifierAreNotUsedInEquals() {
        EqualsVerifier.forClass(NotUsingFieldsWithTransientModifier.class).verify();
    }

    @Test
    public void fail_whenFieldsWithTransientModifierAreUsedInEquals() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(UsingFieldsWithTransientModifier.class).verify())
            .assertFailure()
            .assertMessageContains(
                "Transient field",
                "should not be included in equals/hashCode contract"
            );
    }

    @Test
    public void succeed_whenFieldsWithTransientModifierAreUsedInEquals_givenWarningsAreSuppressed() {
        EqualsVerifier
            .forClass(UsingFieldsWithTransientModifier.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientModifierAreUsedInEquals_givenTheyreDeclaredInSuperclass() {
        ExpectedException
            .when(
                () ->
                    EqualsVerifier.forClass(SubclassUsingFieldsWithTransientModifier.class).verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Transient field",
                "should not be included in equals/hashCode contract"
            );
    }

    @Test
    public void succeed_whenFieldsWithTransientAnnotationAreNotUsedInEquals() {
        EqualsVerifier.forClass(NotUsingFieldsWithTransientAnnotation.class).verify();
    }

    @Test
    public void fail_whenFieldsWithTransientAnnotationAreUsedInEquals() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(UsingFieldsWithTransientAnnotation.class).verify())
            .assertFailure()
            .assertMessageContains(
                "Transient field",
                "should not be included in equals/hashCode contract"
            );
    }

    @Test
    public void succeed_whenFieldsWithTransientAnnotationAreUsedInEquals_givenWarningsAreSuppressed() {
        EqualsVerifier
            .forClass(UsingFieldsWithTransientAnnotation.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .verify();
    }

    @Test
    public void fail_whenFieldsWithTransientAnnotationAreUsedInEquals_givenTheyreDeclaredInSuperclass() {
        ExpectedException
            .when(
                () ->
                    EqualsVerifier
                        .forClass(SubclassUsingFieldsWithTransientAnnotation.class)
                        .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Transient field",
                "should not be included in equals/hashCode contract"
            );
    }

    @Test
    public void fail_whenFieldsWithTransientAnnotationAreNotUsedInEquals_givenAnnotationIsNotAJpaAnnotation() {
        ExpectedException
            .when(
                () ->
                    EqualsVerifier
                        .forClass(NotUsingFieldsWithNonJpaTransientAnnotation.class)
                        .verify()
            )
            .assertFailure()
            .assertMessageContains(
                "Significant fields",
                "equals does not use j, or it is stateless"
            );
    }

    static class NotUsingFieldsWithTransientModifier {

        private final int i;
        private final transient int j;

        public NotUsingFieldsWithTransientModifier(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NotUsingFieldsWithTransientModifier)) {
                return false;
            }
            NotUsingFieldsWithTransientModifier other = (NotUsingFieldsWithTransientModifier) obj;
            return i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }

    static class UsingFieldsWithTransientModifier {

        private final int i;
        private final transient int j;

        public UsingFieldsWithTransientModifier(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof UsingFieldsWithTransientModifier)) {
                return false;
            }
            UsingFieldsWithTransientModifier other = (UsingFieldsWithTransientModifier) obj;
            return i == other.i && j == other.j;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i, j);
        }
    }

    static class SubclassUsingFieldsWithTransientModifier extends UsingFieldsWithTransientModifier {

        public SubclassUsingFieldsWithTransientModifier(int i, int j) {
            super(i, j);
        }
    }

    static class NotUsingFieldsWithTransientAnnotation {

        private final int i;

        @Transient
        private final int j;

        public NotUsingFieldsWithTransientAnnotation(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NotUsingFieldsWithTransientAnnotation)) {
                return false;
            }
            NotUsingFieldsWithTransientAnnotation other = (NotUsingFieldsWithTransientAnnotation) obj;
            return i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }

    static class UsingFieldsWithTransientAnnotation {

        private final int i;

        @Transient
        private final int j;

        public UsingFieldsWithTransientAnnotation(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof UsingFieldsWithTransientAnnotation)) {
                return false;
            }
            UsingFieldsWithTransientAnnotation other = (UsingFieldsWithTransientAnnotation) obj;
            return i == other.i && j == other.j;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i, j);
        }
    }

    static class SubclassUsingFieldsWithTransientAnnotation
        extends UsingFieldsWithTransientAnnotation {

        public SubclassUsingFieldsWithTransientAnnotation(int i, int j) {
            super(i, j);
        }
    }

    static class NotUsingFieldsWithNonJpaTransientAnnotation {

        private final int i;

        @nl.jqno.equalsverifier.testhelpers.annotations.Transient
        private final int j;

        public NotUsingFieldsWithNonJpaTransientAnnotation(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NotUsingFieldsWithNonJpaTransientAnnotation)) {
                return false;
            }
            NotUsingFieldsWithNonJpaTransientAnnotation other = (NotUsingFieldsWithNonJpaTransientAnnotation) obj;
            return i == other.i;
        }

        @Override
        public final int hashCode() {
            return i;
        }
    }
}
