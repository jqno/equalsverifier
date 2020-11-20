package nl.jqno.equalsverifier.integration.extra_features;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullJavax;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

public class AnnotationsIgnoreTest {
    @Test
    public void
            fail_whenClassHasNonfinalFieldsAndImmutableAnnotation_givenImmutableAnnotationIsIgnored() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(ImmutableByAnnotation.class)
                                        .withIgnoredAnnotations(Immutable.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains("Mutability");
    }

    @Test
    public void
            fail_whenIgnoringNonnullAnnotation_givenNonnullIsIndirectlyAppliedThroughDefaultAnnotation() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(DefaultAnnotationNonnull.class)
                                        .withIgnoredAnnotations(Nonnull.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains("Non-nullity");
    }

    @Test
    public void fail_whenIgnoringNonnullAnnotation_givenNonnullIsIndirectlyAppliedThroughJsr305() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(Jsr305Nonnull.class)
                                        .withIgnoredAnnotations(Nonnull.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains("Non-nullity");
    }

    @Test
    public void
            succeed_whenClassHasNonfinalFieldsAndImmutableAnnotation_givenImmutableAnnotationIsIgnored_butItsADifferentImmutableAnnotation() {
        EqualsVerifier.forClass(ImmutableByAnnotation.class)
                .withIgnoredAnnotations(net.jcip.annotations.Immutable.class)
                .verify();
    }

    @Test
    public void fail_whenIgnoredAnnotationClassIsntAnAnnotation() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(ImmutableByAnnotation.class)
                                        .withIgnoredAnnotations(String.class))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("class", "java.lang.String", "is not an annotation");
    }

    @Immutable
    public static final class ImmutableByAnnotation {
        private int i;

        public ImmutableByAnnotation(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultAnnotation(Nonnull.class)
    static final class DefaultAnnotationNonnull {
        private final Object o;

        public DefaultAnnotationNonnull(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DefaultAnnotationNonnull)) {
                return false;
            }
            DefaultAnnotationNonnull other = (DefaultAnnotationNonnull) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @DefaultNonnullJavax
    static final class Jsr305Nonnull {
        private final Object o;

        public Jsr305Nonnull(Object o) {
            this.o = o;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Jsr305Nonnull)) {
                return false;
            }
            Jsr305Nonnull other = (Jsr305Nonnull) obj;
            return o.equals(other.o);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
