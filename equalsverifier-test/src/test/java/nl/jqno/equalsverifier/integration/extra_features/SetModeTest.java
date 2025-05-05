package nl.jqno.equalsverifier.integration.extra_features;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.NodeContainer;
import org.junit.jupiter.api.Test;

public class SetModeTest {
    @Test
    void singleTypeDisable() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NodeContainer.class).set(Mode.skipMockito()).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void multipleTypeDisable() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(NodeContainer.class, Node.class).set(Mode.skipMockito()).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void configuredEqualsVerifierDisable() {
        var ev = EqualsVerifier.configure().set(Mode.skipMockito());
        ExpectedException
                .when(() -> ev.forClass(NodeContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }
}
