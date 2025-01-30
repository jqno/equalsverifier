package it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SanityTest {
    @Test
    void sanity() {
        assertThat(getClass().getModule())
                .extracting(Module::isNamed, Module::getName)
                .containsExactly(true, "equalsverifier_jpms_test");
    }
}
