package nl.jqno.equalsverifier.integration.operational;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecursionTest {

    private static final String RECURSIVE_DATASTRUCTURE = "Recursive datastructure";
    private static final String PREFAB = "Add prefab values for one of the following types";

    private Node red;
    private Node blue;
    private Tree redTree;
    private Tree blueTree;

    @BeforeEach
    public void createSomeNodes() {
        red = new Node(null);
        blue = new Node(new Node(null));
        redTree = new Tree(Collections.<Tree>emptyList());
        blueTree = new Tree(Collections.singletonList(new Tree(Collections.<Tree>emptyList())));
    }

    @Test
    public void fail_whenDatastructureIsRecursive_givenItIsPassedInAsAClass() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(Node.class).verify())
            .assertFailure()
            .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB);
    }

    @Test
    public void succeed_whenDatastructureIsRecursive_givenPrefabValues() {
        EqualsVerifier.forClass(Node.class).withPrefabValues(Node.class, red, blue).verify();
    }

    @Test
    public void succeed_whenDatastructureIsRecursive_givenPrefabValuesOfSuperclass() {
        EqualsVerifier.forClass(SubNode.class).withPrefabValues(Node.class, red, blue).verify();
    }

    @Test
    public void fail_whenFieldIsARecursiveType() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(NodeContainer.class).verify())
            .assertFailure()
            .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB, Node.class.getSimpleName());
    }

    @Test
    public void succeed_whenFieldIsARecursiveType_givenPrefabValues() {
        EqualsVerifier
            .forClass(NodeContainer.class)
            .withPrefabValues(Node.class, red, blue)
            .verify();
    }

    @Test
    public void succeed_whenFieldIsARecursiveType_givenPrefabValuesOfSuperclass() {
        EqualsVerifier
            .forClass(SubNodeContainer.class)
            .withPrefabValues(Node.class, red, blue)
            .verify();
    }

    @Test
    public void fail_whenDatastructureIsRecursiveInGenerics() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(Tree.class).verify())
            .assertFailure()
            .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB);
    }

    @Test
    public void succeed_whenDatastructureIsRecursiveInGenerics_givenPrefabValues() {
        EqualsVerifier
            .forClass(Tree.class)
            .withPrefabValues(Tree.class, redTree, blueTree)
            .verify();
    }

    @Test
    public void fail_whenFieldIsARecursiveTypeInGenerics() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(TreeContainer.class).verify())
            .assertFailure()
            .assertMessageContains(RECURSIVE_DATASTRUCTURE, PREFAB, Tree.class.getSimpleName());
    }

    @Test
    public void succeed_whenFieldIsARecursiveTypeInGenerics_givenPrefabValues() {
        EqualsVerifier
            .forClass(TreeContainer.class)
            .withPrefabValues(Tree.class, redTree, blueTree)
            .verify();
    }

    @Test
    public void giveCorrectErrorMessage_whenFieldIsInstantiatedUsingReflectiveFactory() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(ImmutableListTree.class).verify())
            .assertFailure()
            .assertMessageContains(
                RECURSIVE_DATASTRUCTURE,
                ImmutableListTree.class.getSimpleName(),
                new TypeTag(ImmutableList.class, new TypeTag(ImmutableListTree.class)).toString(),
                new TypeTag(Collection.class, new TypeTag(ImmutableListTree.class)).toString()
            ); // I'd prefer not to have this last one though.
    }

    @Test
    public void succeed_whenStaticFinalFieldIsRecursive_givenNoPrefabValues() {
        EqualsVerifier.forClass(StaticFinalNodeContainer.class).verify();
    }

    static class Node {

        final Node node;

        public Node(Node node) {
            this.node = node;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node other = (Node) obj;
            return Objects.equals(node, other.node);
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class SubNode extends Node {

        public SubNode(Node node) {
            super(node);
        }
    }

    static class NodeContainer {

        final Node node;

        public NodeContainer(Node node) {
            this.node = node;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NodeContainer)) {
                return false;
            }
            NodeContainer other = (NodeContainer) obj;
            return Objects.equals(node, other.node);
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class SubNodeContainer extends NodeContainer {

        public SubNodeContainer(Node node) {
            super(node);
        }
    }

    static class Tree {

        final List<Tree> innerTrees;

        public Tree(List<Tree> innerTrees) {
            this.innerTrees = innerTrees;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof Tree)) {
                return false;
            }
            Tree other = (Tree) obj;
            return Objects.equals(innerTrees, other.innerTrees);
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class TreeContainer {

        final Tree tree;

        public TreeContainer(Tree tree) {
            this.tree = tree;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof TreeContainer)) {
                return false;
            }
            TreeContainer other = (TreeContainer) obj;
            return Objects.equals(tree, other.tree);
        }

        @Override
        public final int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class ImmutableListTree {

        final ImmutableList<ImmutableListTree> tree;

        public ImmutableListTree(ImmutableList<ImmutableListTree> tree) {
            this.tree = tree;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    @SuppressWarnings("unused")
    static final class StaticFinalNodeContainer {

        private static final Node NODE = new Node(null);
        private final int i;

        public StaticFinalNodeContainer(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
