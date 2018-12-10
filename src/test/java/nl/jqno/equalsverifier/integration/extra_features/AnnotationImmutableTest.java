package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.types.ImmutableCanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.types.MutableCanEqualColorPoint;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class AnnotationImmutableTest extends ExpectedExceptionTestBase {
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

    @Immutable
    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    public static final class ImmutableByAnnotation {
        private int i;

        public ImmutableByAnnotation(int i) { this.i = i; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
