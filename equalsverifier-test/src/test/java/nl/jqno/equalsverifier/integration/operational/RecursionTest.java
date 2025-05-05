package nl.jqno.equalsverifier.integration.operational;

import java.util.Collections;
import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecursionTest {

    private static final String RECURSIVE_DATASTRUCTURE = "Recursive datastructure";
    private static final String PREFAB = "Add prefab values for one of the following types";

    private Node red;
    private Node blue;
    private Tree redTree;
    private Tree blueTree;

    @BeforeEach
    void createSomeNodes() {
        red = new Node(null);
        blue = new Node(new Node(null));
        redTree = new Tree(Collections.<Tree>emptyList());
        blueTree = new Tree(List.of(new Tree(Collections.<Tree>emptyList())));
    }

    @Test
    void fail_whenDatastructureIsRecursive_givenItIsPassedInAsAClass() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(Node.class).verify())
                .assertFailure()
                .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB);
    }

    @Test
    void succeed_whenDatastructureIsRecursive_givenPrefabValues() {
        EqualsVerifier.forClass(Node.class).withPrefabValues(Node.class, red, blue).verify();
    }

    @Test
    void succeed_whenDatastructureIsRecursive_givenPrefabValuesForField() {
        EqualsVerifier.forClass(Node.class).withPrefabValuesForField("node", red, blue).verify();
    }

    @Test
    void succeed_whenDatastructureIsRecursive_givenPrefabValuesOfSuperclass() {
        EqualsVerifier.forClass(SubNode.class).withPrefabValues(Node.class, red, blue).verify();
    }

    @Test
    void succeed_whenDatastructureIsRecursive_givenPrefabValuesForFieldOfSuperclass() {
        EqualsVerifier.forClass(SubNode.class).withPrefabValuesForField("node", red, blue).verify();
    }

    @Test
    void fail_whenFieldIsARecursiveType() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NodeContainer.class).verify())
                .assertFailure()
                .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB, Node.class.getSimpleName());
    }

    @Test
    void succeed_whenFieldIsARecursiveType_givenPrefabValues() {
        EqualsVerifier.forClass(NodeContainer.class).withPrefabValues(Node.class, red, blue).verify();
    }

    @Test
    void succeed_whenFieldIsARecursiveType_givenPrefabValuesForField() {
        EqualsVerifier.forClass(NodeContainer.class).withPrefabValuesForField("node", red, blue).verify();
    }

    @Test
    void succeed_whenFieldIsARecursiveType_givenPrefabValuesOfSuperclass() {
        EqualsVerifier.forClass(SubNodeContainer.class).withPrefabValues(Node.class, red, blue).verify();
    }

    @Test
    void succeed_whenFieldIsARecursiveType_givenPrefabValuesForFieldOfSuperclass() {
        EqualsVerifier.forClass(SubNodeContainer.class).withPrefabValuesForField("node", red, blue).verify();
    }

    @Test
    void fail_whenDatastructureIsRecursiveInGenerics() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(Tree.class).verify())
                .assertFailure()
                .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB);
    }

    @Test
    void succeed_whenDatastructureIsRecursiveInGenerics_givenPrefabValues() {
        EqualsVerifier.forClass(Tree.class).withPrefabValues(Tree.class, redTree, blueTree).verify();
    }

    @Test
    void fail_whenFieldIsARecursiveTypeInGenerics() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(TreeContainer.class).verify())
                .assertFailure()
                .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB, Tree.class.getSimpleName());
    }

    @Test
    void succeed_whenFieldIsARecursiveTypeInGenerics_givenPrefabValues() {
        EqualsVerifier.forClass(TreeContainer.class).withPrefabValues(Tree.class, redTree, blueTree).verify();
    }

    @Test
    void succeed_whenStaticFinalFieldIsRecursive_givenNoPrefabValues() {
        EqualsVerifier.forClass(StaticFinalNodeContainer.class).verify();
    }
}
