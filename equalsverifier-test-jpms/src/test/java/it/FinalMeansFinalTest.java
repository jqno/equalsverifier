package it;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.jpms.nonreflectable.NonReflectable;
import org.junit.jupiter.api.Test;

public class FinalMeansFinalTest {
    @Test
    void giveCorrectErrorMessage_whenConstructorCantBeCalled() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(NonReflectable.class).set(Mode.finalMeansFinal()).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate NonReflectable")
                .hasMessageContaining("Use #withFactory()");
    }
}
