package nl.jqno.equalsverifier.integration.extra_features;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.reflection.Util;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.GetClassPoint;
import org.junit.jupiter.api.Test;

public class OptionalDependencySanityTest {
    @Test
    void mockitoIsUnavailable() {
        var mockito = Util.classForName("org.mockito.Mockito");
        assertThat(mockito).isNull();
    }

    @Test
    void mockitoIsCorrectlyReported() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(GetClassPoint.class).verify())
                .assertFailure()
                .assertMessageContains("Mockito: not available");
    }
}
