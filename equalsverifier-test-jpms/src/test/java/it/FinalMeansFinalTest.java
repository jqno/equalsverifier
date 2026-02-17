package it;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.jpms.inaccessible.FinalClassWithInaccessiblePrivateConstructor;
import nl.jqno.equalsverifier.jpms.inaccessible.JpmsInaccessible;
import nl.jqno.equalsverifier.jpms.model.FinalClassWithPrivateConstructor;
import org.junit.jupiter.api.Test;

public class FinalMeansFinalTest {
    @Test
    void giveCorrectErrorMessage_whenConstructorCantBeCalled() {
        assertThatThrownBy(() -> EqualsVerifier.forClass(JpmsInaccessible.class).set(Mode.finalMeansFinal()).verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(
                    "The class is not accessible via the Java Module system. Consider opening the module that contains it.");
    }

    @Test
    void succeed_whenConstructorIsPrivate_givenModuleIsOpen() {
        EqualsVerifier.forClass(FinalClassWithPrivateConstructor.class).set(Mode.finalMeansFinal()).verify();
    }

    @Test
    void succeed_whenConstructorIsPrivateAndInaccessible_givenModuleIsOpen() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(FinalClassWithInaccessiblePrivateConstructor.class)
                    .set(Mode.finalMeansFinal())
                    .verify())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Cannot instantiate FinalClassWithInaccessiblePrivateConstructor")
                .hasMessageContaining("Use #withFactory()");
    }
}
