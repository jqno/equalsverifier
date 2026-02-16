package it;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.jpms.inaccessible.JpmsInaccessible;
import org.junit.jupiter.api.Test;

public class FinalMeansFinalTest {
    @Test
    void giveCorrectErrorMessage_whenConstructorCantBeCalled() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(JpmsInaccessible.class).set(Mode.finalMeansFinal()).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate JpmsInaccessible")
                .hasMessageContaining("Use #withFactory()");
    }
}
