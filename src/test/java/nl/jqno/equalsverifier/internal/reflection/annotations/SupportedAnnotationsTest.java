package nl.jqno.equalsverifier.internal.reflection.annotations;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SupportedAnnotationsTest {
    private static final Set<String> NO_IGNORED_ANNOTATIONS = new HashSet<>();
    private static final AnnotationCache EMPTY_ANNOTATION_CACHE = new AnnotationCache();

    @Test
    public void jsr305DefaultReturnsTrue_whenAnnotationHasNonnullAnnotation() {
        AnnotationProperties props = new AnnotationProperties("Lnl/jqno/equalsverifier/testhelpers/annotations/DefaultNonnullJavax;");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(props, EMPTY_ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertTrue(actual);
    }

    @Test
    public void jsr305DefaultReturnsFalse_whenAnnotationDoesntHaveNonnullAnnotation() {
        AnnotationProperties props = new AnnotationProperties("Ljavax/annotation/Nonnull;");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(props, EMPTY_ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertFalse(actual);
    }

    @Test
    public void jsr305DefaultReturnsFalse_whenTypeDoesNotExist() {
        AnnotationProperties props = new AnnotationProperties("Lnl/jqno/equalsverifier/TypeDoesNotExist;");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(props, EMPTY_ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertFalse(actual);
    }
}
