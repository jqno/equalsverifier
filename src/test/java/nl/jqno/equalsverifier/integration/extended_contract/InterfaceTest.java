package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class InterfaceTest {
    @Test
    public void succeed_whenClassIsAnInterface() {
        EqualsVerifier.forClass(CharSequence.class).verify();
    }

    @Test
    public void succeed_whenClassIsAnEmptyInterface() {
        EqualsVerifier.forClass(Interface.class).verify();
    }

    interface Interface {}
}
