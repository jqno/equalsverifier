package nl.jqno.equalsverifier.mockito;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.*;
import org.junit.jupiter.api.Test;

public class MockitoRecursionTest {

    @Test
    void verifyRecursiveClass() {
        EqualsVerifier.forClass(Node.class).verify();
    }

    @Test
    void verifyRecursiveContainer() {
        EqualsVerifier.forClass(NodeContainer.class).verify();
    }

    @Test
    void verifyRecursiveSubContainer() {
        EqualsVerifier.forClass(SubNodeContainer.class).verify();
    }

    @Test
    void verifyRecursiveGeneric() {
        EqualsVerifier.forClass(Tree.class).verify();
    }

    @Test
    void verifyRecursiveGenericContainer() {
        EqualsVerifier.forClass(TreeContainer.class).verify();
    }

    @Test
    void verifyRecursiveArray() {
        EqualsVerifier.forClass(NodeArray.class).verify();
    }

    @Test
    void verifyTwoStepRecursiveClass() {
        EqualsVerifier.forClass(TwoStepNodeA.class).verify();
    }

    @Test
    void verifyTwoStepRecursiveArray() {
        EqualsVerifier.forClass(TwoStepNodeArrayA.class).verify();
    }

}
