package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.springframework.NonNullFieldsOnPackage;
import org.junit.jupiter.api.Test;

class AnnotationNonNullFieldsTest {

    @Test
    void succeed_whenEqualsDoesntCheckForNull_givenNonNullFieldsAnnotationOnPackage() {
        EqualsVerifier.forClass(NonNullFieldsOnPackage.class).verify();
    }
}
