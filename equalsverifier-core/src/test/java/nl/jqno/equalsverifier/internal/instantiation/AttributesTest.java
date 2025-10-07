package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttributesTest {

    @Test
    void emptyFactory_createsCorrectInstance() {
        var attrs = Attributes.empty();
        assertThat(attrs.fieldName()).isNull();
        assertThat(attrs.typeStack()).isEmpty();
    }

    @Test
    void namedFactory_createsCorrectInstance() {
        var fieldName = "testField";
        var attrs = Attributes.named(fieldName);
        assertThat(attrs.fieldName()).isEqualTo(fieldName);
        assertThat(attrs.typeStack()).isEmpty();
    }
}
