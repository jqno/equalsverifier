package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ModuleTest {
    @Test
    void sanity() {
        // We want the tests in this module to be run on the class path, not the module path.
        assertThat(getClass().getModule().isNamed()).isFalse();
    }
}
