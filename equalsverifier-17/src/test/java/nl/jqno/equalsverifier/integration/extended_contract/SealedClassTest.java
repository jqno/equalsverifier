package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.integration.extended_contract.SealedClasses.SealedChild;
import nl.jqno.equalsverifier.integration.extended_contract.SealedClasses.SealedParent;
import org.junit.jupiter.api.Test;

public class SealedClassTest {

    @Test
    public void dontCrashOnParent() {
        EqualsVerifier
            .forClass(SealedParent.class)
            .withRedefinedSubclass(SealedChild.class)
            .verify();
    }

    @Test
    public void dontCrashOnChild() {
        EqualsVerifier.forClass(SealedChild.class).withRedefinedSuperclass().verify();
    }
}
