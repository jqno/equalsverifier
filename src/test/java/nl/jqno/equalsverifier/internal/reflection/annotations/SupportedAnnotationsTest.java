package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.internal.reflection.Util.setOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SupportedAnnotationsTest {
    private static final Set<String> NO_IGNORED_ANNOTATIONS = new HashSet<>();
    private static final AnnotationCache ANNOTATION_CACHE = new AnnotationCache();

    @Test
    public void jsr305DefaultReturnsTrue_whenAnnotationHasNonnullAnnotation() {
        AnnotationProperties props =
                new AnnotationProperties(
                        "nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullJavax");
        boolean actual =
                SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(
                        props, ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertTrue(actual);
    }

    @Test
    public void jsr305DefaultReturnsFalse_whenAnnotationDoesntHaveNonnullAnnotation() {
        AnnotationProperties props = new AnnotationProperties("javax.annotation.Nonnull");
        boolean actual =
                SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(
                        props, ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertFalse(actual);
    }

    @Test
    public void jsr305DefaultReturnsFalse_whenTypeDoesNotExist() {
        AnnotationProperties props =
                new AnnotationProperties("nl.jqno.equalsverifier.TypeDoesNotExist");
        boolean actual =
                SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(
                        props, ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertFalse(actual);
    }

    @Test
    public void jpaIdAnnotationIsAlsoAddedAsAClassAnnotation() {
        SupportedAnnotations.ID.postProcess(setOf(String.class), ANNOTATION_CACHE);
        assertTrue(ANNOTATION_CACHE.hasClassAnnotation(String.class, SupportedAnnotations.ID));
    }

    @Test
    public void hibernateNaturalIdAnnotationIsAlsoAddedAsAClassAnnotation() {
        SupportedAnnotations.NATURALID.postProcess(setOf(String.class), ANNOTATION_CACHE);
        assertTrue(
                ANNOTATION_CACHE.hasClassAnnotation(String.class, SupportedAnnotations.NATURALID));
    }
}
