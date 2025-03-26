package nl.jqno.equalsverifier.integration.extra_features;

import static org.assertj.core.api.Assertions.assertThat;

import nl.jqno.equalsverifier.internal.reflection.Util;
import org.junit.jupiter.api.Test;

public class OptionalDependencySanityTest {
    @Test
    void mockitoIsUnavailable() {
        var mockito = Util.classForName("org.mockito.Mockito");
        assertThat(mockito).isNull();
    }
}
