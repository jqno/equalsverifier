package nl.jqno.equalsverifier.integration.extra_features;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class JakartaEntityTest {

    @Test
    void succeed_whenClassIsNonFinalAndFieldsAreMutable_givenClassHasJpaEntityAnnotation() {
        EqualsVerifier.forClass(EntityByJakartaAnnotation.class).verify();
    }

    @Test
    void fail_whenClassIsNonFinalAndFieldsAreMutable_givenSuperclassHasJpaEntityAnnotationButThisClassDoesnt() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SubclassEntityByJakartaAnnotation.class).verify())
                .assertFailure()
                .assertMessageContains("Subclass");
    }

    @Test
    void succeed_whenFieldsAreMutable_givenClassHasJpaEmbeddableAnnotation() {
        EqualsVerifier.forClass(EmbeddableByJakartaAnnotation.class).verify();
    }

    @Test
    void fail_whenFieldsAreMutable_givenSuperclassHasJpaEmbeddableAnnotationButThisClassDoesnt() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SubclassEmbeddableByJakartaAnnotation.class).verify())
                .assertFailure()
                .assertMessageContains("Subclass");
    }

    @Test
    void succeed_whenFieldsAreMutable_givenClassHasJpaMappedSuperclassAnnotation() {
        EqualsVerifier.forClass(MappedSuperclassByJakartaAnnotation.class).verify();
    }

    @Test
    void fail_whenFieldsAreMutable_givenSuperclassHasJpaMappedSuperclassAnnotationButThisClassDoesnt() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SubclassMappedSuperclassByJakartaAnnotation.class).verify())
                .assertFailure()
                .assertMessageContains("Subclass");
    }

    @jakarta.persistence.Entity
    static class EntityByJakartaAnnotation {

        private int i;
        private String s;

        public void setI(int value) {
            this.i = value;
        }

        public void setS(String value) {
            this.s = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EntityByJakartaAnnotation)) {
                return false;
            }
            EntityByJakartaAnnotation other = (EntityByJakartaAnnotation) obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    static class SubclassEntityByJakartaAnnotation extends EntityByJakartaAnnotation {}

    @jakarta.persistence.Embeddable
    static class EmbeddableByJakartaAnnotation {

        private int i;
        private String s;

        public void setI(int value) {
            this.i = value;
        }

        public void setS(String value) {
            this.s = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EmbeddableByJakartaAnnotation)) {
                return false;
            }
            EmbeddableByJakartaAnnotation other = (EmbeddableByJakartaAnnotation) obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    static class SubclassEmbeddableByJakartaAnnotation extends EmbeddableByJakartaAnnotation {}

    @jakarta.persistence.MappedSuperclass
    abstract static class MappedSuperclassByJakartaAnnotation {

        private int i;
        private String s;

        public void setI(int value) {
            this.i = value;
        }

        public void setS(String value) {
            this.s = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MappedSuperclassByJakartaAnnotation)) {
                return false;
            }
            MappedSuperclassByJakartaAnnotation other = (MappedSuperclassByJakartaAnnotation) obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, s);
        }
    }

    static class SubclassMappedSuperclassByJakartaAnnotation extends MappedSuperclassByJakartaAnnotation {}
}
