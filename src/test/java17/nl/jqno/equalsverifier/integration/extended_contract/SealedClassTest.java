package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.integration.extended_contract.SealedClasses.SealedChild;
import org.junit.jupiter.api.Test;

public class SealedClassTest {

    @Test
    public void dontCrash() {
        EqualsVerifier.forClass(SealedChild.class).withRedefinedSuperclass().verify();
    }
}
