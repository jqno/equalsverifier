package nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SimpleFactoryTest {

    private final SimpleFactory<String> factory = new SimpleFactory<>("red", "blue", new String("red"));

    @Test
    void createRed() {
        assertThat(factory.createValues(null, null, null).getRed()).isEqualTo("red");
    }

    @Test
    void createBlue() {
        assertThat(factory.createValues(null, null, null).getBlue()).isEqualTo("blue");
    }

    @Test
    void redCopy() {
        String redCopy = factory.createValues(null, null, null).getRedCopy();
        assertThat(redCopy).isEqualTo("red").isNotSameAs("red");
    }
}
