/*
 * Copyright 2009-2010, 2014 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.integration.operational;

import com.google.common.collect.ImmutableList;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class RecursionTest extends IntegrationTestBase {
    private static final String RECURSIVE_DATASTRUCTURE = "Recursive datastructure";
    private static final String PREFAB = "Add prefab values for one of the following types";

    private Node red;
    private Node black;
    private Tree redTree;
    private Tree blackTree;

    @Before
    public void createSomeNodes() {
        red = new Node(null);
        black = new Node(new Node(null));
        redTree = new Tree(Collections.<Tree>emptyList());
        blackTree = new Tree(Collections.singletonList(new Tree(Collections.<Tree>emptyList())));
    }

    @Test
    public void fail_whenDatastructureIsRecursive_givenItIsPassedInAsAClass() {
        expectFailure(RECURSIVE_DATASTRUCTURE, PREFAB);
        EqualsVerifier.forClass(Node.class)
                .verify();
    }

    @Test
    public void fail_whenDatastructureIsRecursive_givenItIsPassedInAsExamples() {
        expectFailure(RECURSIVE_DATASTRUCTURE, PREFAB);
        EqualsVerifier.forExamples(red, black)
                .verify();
    }

    @Test
    public void succeed_whenDatastructureIsRecursive_givenPrefabValues() {
        EqualsVerifier.forClass(Node.class)
                .withPrefabValues(Node.class, red, black)
                .verify();
    }

    @Test
    public void succeed_whenDatastructureIsRecursive_givenPrefabValuesOfSuperclass() {
        EqualsVerifier.forClass(SubNode.class)
                .withPrefabValues(Node.class, red, black)
                .verify();
    }

    @Test
    public void fail_whenFieldIsARecursiveType() {
        expectFailure(RECURSIVE_DATASTRUCTURE, PREFAB, Node.class.getSimpleName());
        EqualsVerifier.forClass(NodeContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldIsARecursiveType_givenPrefabValues() {
        EqualsVerifier.forClass(NodeContainer.class)
                .withPrefabValues(Node.class, red, black)
                .verify();
    }

    @Test
    public void succeed_whenFieldIsARecursiveType_givenPrefabValuesOfSuperclass() {
        EqualsVerifier.forClass(SubNodeContainer.class)
                .withPrefabValues(Node.class, red, black)
                .verify();
    }

    @Test
    public void fail_whenDatastructureIsRecursiveInGenerics() {
        expectFailure(RECURSIVE_DATASTRUCTURE, PREFAB);
        EqualsVerifier.forClass(Tree.class)
                .verify();
    }

    @Test
    public void succeed_whenDatastructureIsRecursiveInGenerics_givenPrefabValues() {
        EqualsVerifier.forClass(Tree.class)
                .withPrefabValues(Tree.class, redTree, blackTree)
                .verify();
    }

    @Test
    public void fail_whenFieldIsARecursiveTypeInGenerics() {
        expectFailure(RECURSIVE_DATASTRUCTURE, PREFAB, Tree.class.getSimpleName());
        EqualsVerifier.forClass(TreeContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldIsARecursiveTypeInGenerics_givenPrefabValues() {
        EqualsVerifier.forClass(TreeContainer.class)
                .withPrefabValues(Tree.class, redTree, blackTree)
                .verify();
    }

    @Test
    public void giveCorrectErrorMessage_whenFieldIsInstantiatedUsingReflectiveFactory() {
        expectFailure(RECURSIVE_DATASTRUCTURE, ImmutableListTree.class.getSimpleName(),
                new TypeTag(ImmutableList.class, new TypeTag(ImmutableListTree.class)).toString(),
                new TypeTag(Collection.class, new TypeTag(ImmutableListTree.class)).toString()); // I'd prefer not to have this last one though.
        EqualsVerifier.forClass(ImmutableListTree.class)
                .verify();
    }

    static class Node {
        final Node node;

        public Node(Node node) { this.node = node; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node other = (Node)obj;
            return Objects.equals(node, other.node);
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static class SubNode extends Node {
        public SubNode(Node node) {
            super(node);
        }
    }

    static class NodeContainer {
        final Node node;

        public NodeContainer(Node node) { this.node = node; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NodeContainer)) {
                return false;
            }
            NodeContainer other = (NodeContainer)obj;
            return Objects.equals(node, other.node);
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static class SubNodeContainer extends NodeContainer {
        public SubNodeContainer(Node node) {
            super(node);
        }
    }

    static class Tree {
        final List<Tree> innerTrees;

        public Tree(List<Tree> innerTrees) { this.innerTrees = innerTrees; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof Tree)) {
                return false;
            }
            Tree other = (Tree)obj;
            return Objects.equals(innerTrees, other.innerTrees);
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static class TreeContainer {
        final Tree tree;

        public TreeContainer(Tree tree) { this.tree = tree; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof TreeContainer)) {
                return false;
            }
            TreeContainer other = (TreeContainer)obj;
            return Objects.equals(tree, other.tree);
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    static final class ImmutableListTree {
        final ImmutableList<ImmutableListTree> tree;

        public ImmutableListTree(ImmutableList<ImmutableListTree> tree) { this.tree = tree; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
