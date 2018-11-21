package equalsverifier.integration.extra_features;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import equalsverifier.EqualsVerifier;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.annotations.DefaultNonnullJavax;
import equalsverifier.testhelpers.annotations.Immutable;
import equalsverifier.testhelpers.annotations.javax.annotation.Nonnull;
import org.junit.Test;

import static equalsverifier.testhelpers.Util.defaultEquals;
import static equalsverifier.testhelpers.Util.defaultHashCode;

public class AnnotationsIgnoreTest extends ExpectedExceptionTestBase {
    @Test
    public void fail_whenClassHasNonfinalFieldsAndImmutableAnnotation_givenImmutableAnnotationIsIgnored() {
        expectFailure("Mutability");
        EqualsVerifier.forClass(ImmutableByAnnotation.class)
                .withIgnoredAnnotations(Immutable.class)
                .verify();
    }

    @Test
    public void fail_whenIgnoringNonnullAnnotation_givenNonnullIsIndirectlyAppliedThroughDefaultAnnotation() {
        expectFailure("Non-nullity");
        EqualsVerifier.forClass(DefaultAnnotationNonnull.class)
                .withIgnoredAnnotations(Nonnull.class)
                .verify();
    }

    @Test
    public void fail_whenIgnoringNonnullAnnotation_givenNonnullIsIndirectlyAppliedThroughJsr305() {
        expectFailure("Non-nullity");
        EqualsVerifier.forClass(Jsr305Nonnull.class)
                .withIgnoredAnnotations(Nonnull.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasNonfinalFieldsAndImmutableAnnotation_givenImmutableAnnotationIsIgnored_butItsADifferentImmutableAnnotation() {
        EqualsVerifier.forClass(ImmutableByAnnotation.class)
                .withIgnoredAnnotations(net.jcip.annotations.Immutable.class)
                .verify();
    }

    @Test
    public void fail_whenIgnoredAnnotationClassIsntAnAnnotation() {
        expectException(IllegalArgumentException.class, "Class", "java.lang.String", "is not an annotation");
        EqualsVerifier.forClass(ImmutableByAnnotation.class)
                .withIgnoredAnnotations(String.class);
    }

    @Immutable
    public static final class ImmutableByAnnotation {
        private int i;

        public ImmutableByAnnotation(int i) { this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    @DefaultAnnotation(Nonnull.class)
    static final class DefaultAnnotationNonnull {
        private final Object o;

        public DefaultAnnotationNonnull(Object o) { this.o = o; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DefaultAnnotationNonnull)) {
                return false;
            }
            DefaultAnnotationNonnull other = (DefaultAnnotationNonnull)obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() { return defaultHashCode(this); }
    }

    @DefaultNonnullJavax
    static final class Jsr305Nonnull {
        private final Object o;

        public Jsr305Nonnull(Object o) { this.o = o; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Jsr305Nonnull)) {
                return false;
            }
            Jsr305Nonnull other = (Jsr305Nonnull)obj;
            return o.equals(other.o);
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
