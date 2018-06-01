package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.types.ImmutableCanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.types.MutableCanEqualColorPoint;
import org.junit.Test;

import java.util.Objects;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class AnnotationsTest extends IntegrationTestBase {
    @Test
    public void succeed_whenClassHasNonfinalFields_givenImmutableAnnotation() {
        EqualsVerifier.forClass(ImmutableByAnnotation.class)
                .verify();
    }

    @Test
    public void succeed_whenRedefinableClassHasNonfinalFields_givenImmutableAnnotationAndAppropriateSubclass() {
        EqualsVerifier.forClass(ImmutableCanEqualPoint.class)
                .withRedefinedSubclass(MutableCanEqualColorPoint.class)
                .verify();
    }

    @Test
    public void fail_whenSuperclassHasImmutableAnnotationButThisClassDoesnt() {
        expectFailure("Mutability", "equals depends on mutable field", "color");
        EqualsVerifier.forClass(MutableCanEqualColorPoint.class)
                .withRedefinedSuperclass()
                .verify();
    }

    @Test
    public void succeed_whenClassIsNonFinalAndFieldsAreMutable_givenClassHasJpaEntityAnnotation() {
        EqualsVerifier.forClass(EntityByJpaAnnotation.class)
                .verify();
    }

    @Test
    public void fail_whenClassIsNonFinalAndFieldsAreMutable_givenSuperclassHasJpaEntityAnnotationButThisClassDoesnt() {
        expectFailure("Subclass");
        EqualsVerifier.forClass(SubclassEntityByJpaAnnotation.class)
                .verify();
    }

    @Test
    public void fail_whenClassIsJpaEntity_givenEntityAnnotationResidesInWrongPackage() {
        expectFailure("Subclass");
        EqualsVerifier.forClass(EntityByNonJpaAnnotation.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldsAreMutable_givenClassHasJpaEmbeddableAnnotation() {
        EqualsVerifier.forClass(EmbeddableByJpaAnnotation.class)
                .verify();
    }

    @Test
    public void fail_whenFieldsAreMutable_givenSuperclassHasJpaEmbeddableAnnotationButThisClassDoesnt() {
        expectFailure("Subclass");
        EqualsVerifier.forClass(SubclassEmbeddableByJpaAnnotation.class)
                .verify();
    }

    @Test
    public void fail_whenClassIsJpaEmbeddable_givenEmbeddableAnnotationResidesInWrongPackage() {
        expectFailure("Subclass");
        EqualsVerifier.forClass(EmbeddableByNonJpaAnnotation.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldsAreMutable_givenClassHasJpaMappedSuperclassAnnotation() {
        EqualsVerifier.forClass(MappedSuperclassByJpaAnnotation.class)
                .verify();
    }

    @Test
    public void fail_whenFieldsAreMutable_givenSuperclassHasJpaMappedSuperclassAnnotationButThisClassDoesnt() {
        expectFailure("Subclass");
        EqualsVerifier.forClass(SubclassMappedSuperclassByJpaAnnotation.class)
                .verify();
    }

    @Test
    public void fail_whenClassIsJpaMappedSuperclass_givenMappedSuperclassAnnotationResidesInWrongPackage() {
        expectFailure("Subclass");
        EqualsVerifier.forClass(MappedSuperclassByNonJpaAnnotation.class)
                .verify();
    }

    @Immutable
    public static final class ImmutableByAnnotation {
        private int i;

        public ImmutableByAnnotation(int i) { this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Entity
    static class EntityByJpaAnnotation {
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
            if (!(obj instanceof EntityByJpaAnnotation)) {
                return false;
            }
            EntityByJpaAnnotation other = (EntityByJpaAnnotation)obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static class SubclassEntityByJpaAnnotation extends EntityByJpaAnnotation {}

    @nl.jqno.equalsverifier.testhelpers.annotations.Entity
    static class EntityByNonJpaAnnotation {
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
            if (!(obj instanceof EntityByNonJpaAnnotation)) {
                return false;
            }
            EntityByNonJpaAnnotation other = (EntityByNonJpaAnnotation)obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.Embeddable
    static class EmbeddableByJpaAnnotation {
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
            if (!(obj instanceof EmbeddableByJpaAnnotation)) {
                return false;
            }
            EmbeddableByJpaAnnotation other = (EmbeddableByJpaAnnotation)obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static class SubclassEmbeddableByJpaAnnotation extends EmbeddableByJpaAnnotation {}

    @nl.jqno.equalsverifier.testhelpers.annotations.Embeddable
    static class EmbeddableByNonJpaAnnotation {
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
            if (!(obj instanceof EmbeddableByNonJpaAnnotation)) {
                return false;
            }
            EmbeddableByNonJpaAnnotation other = (EmbeddableByNonJpaAnnotation)obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @nl.jqno.equalsverifier.testhelpers.annotations.javax.persistence.MappedSuperclass
    abstract static class MappedSuperclassByJpaAnnotation {
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
            if (!(obj instanceof MappedSuperclassByJpaAnnotation)) {
                return false;
            }
            MappedSuperclassByJpaAnnotation other = (MappedSuperclassByJpaAnnotation)obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static class SubclassMappedSuperclassByJpaAnnotation extends MappedSuperclassByJpaAnnotation {}

    @nl.jqno.equalsverifier.testhelpers.annotations.MappedSuperclass
    abstract static class MappedSuperclassByNonJpaAnnotation {
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
            if (!(obj instanceof MappedSuperclassByNonJpaAnnotation)) {
                return false;
            }
            MappedSuperclassByNonJpaAnnotation other = (MappedSuperclassByNonJpaAnnotation)obj;
            return i == other.i && Objects.equals(s, other.s);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
