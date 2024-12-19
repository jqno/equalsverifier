package nl.jqno.equalsverifier.internal.reflection.annotations;

import static nl.jqno.equalsverifier.internal.reflection.Util.setOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SupportedAnnotationsTest {

    private static final Set<String> NO_IGNORED_ANNOTATIONS = new HashSet<>();
    private static final AnnotationCache ANNOTATION_CACHE = new AnnotationCache();

    @Test
    void jsr305DefaultReturnsTrue_whenAnnotationHasNonnullAnnotation() {
        AnnotationProperties props =
                new AnnotationProperties("nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullJavax");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL
                .validate(props, ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertThat(actual).isTrue();
    }

    @Test
    void jsr305DefaultReturnsFalse_whenAnnotationDoesntHaveNonnullAnnotation() {
        AnnotationProperties props = new AnnotationProperties("javax.annotation.Nonnull");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL
                .validate(props, ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertThat(actual).isFalse();
    }

    @Test
    void jsr305DefaultReturnsFalse_whenTypeDoesNotExist() {
        AnnotationProperties props = new AnnotationProperties("nl.jqno.equalsverifier.TypeDoesNotExist");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL
                .validate(props, ANNOTATION_CACHE, NO_IGNORED_ANNOTATIONS);
        assertThat(actual).isFalse();
    }

    @Test
    void jpaIdAnnotationIsAlsoAddedAsAClassAnnotation() {
        SupportedAnnotations.ID.postProcess(setOf(String.class), ANNOTATION_CACHE);
        assertThat(ANNOTATION_CACHE.hasClassAnnotation(String.class, SupportedAnnotations.ID)).isTrue();
    }

    @Test
    void hibernateNaturalIdAnnotationIsAlsoAddedAsAClassAnnotation() {
        SupportedAnnotations.NATURALID.postProcess(setOf(String.class), ANNOTATION_CACHE);
        assertThat(ANNOTATION_CACHE.hasClassAnnotation(String.class, SupportedAnnotations.NATURALID)).isTrue();
    }
}
