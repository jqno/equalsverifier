package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class InterfaceTest {

    @Test
    void succeed_whenClassIsAnInterface() {
        EqualsVerifier.forClass(CharSequence.class).verify();
    }

    @Test
    void succeed_whenClassIsAnEmptyInterface() {
        EqualsVerifier.forClass(Interface.class).verify();
    }

    interface Interface {}
}
