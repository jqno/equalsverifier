package nl.jqno.equalsverifier.integration.extra_features;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.types.ImmutableCanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.types.MutableCanEqualColorPoint;
import org.junit.jupiter.api.Test;

public class AnnotationImmutableTest {
    @Test
    public void succeed_whenClassHasNonfinalFields_givenImmutableAnnotation() {
        EqualsVerifier.forClass(ImmutableByAnnotation.class).verify();
    }

    @Test
    public void
            succeed_whenRedefinableClassHasNonfinalFields_givenImmutableAnnotationAndAppropriateSubclass() {
        EqualsVerifier.forClass(ImmutableCanEqualPoint.class)
                .withRedefinedSubclass(MutableCanEqualColorPoint.class)
                .verify();
    }

    @Test
    public void fail_whenSuperclassHasImmutableAnnotationButThisClassDoesnt() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(MutableCanEqualColorPoint.class)
                                        .withRedefinedSuperclass()
                                        .verify())
                .assertFailure()
                .assertMessageContains("Mutability", "equals depends on mutable field", "color");
    }

    @Immutable
    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
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
}
