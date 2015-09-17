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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class RecursionTest extends IntegrationTestBase {
    private static final String RECURSIVE_DATASTRUCTURE = "Recursive datastructure";
    private static final String PREFAB = "Add prefab values for one of the following types";

    private Node red;
    private Node black;

    @Before
    public void createSomeNodes() {
        red = new Node(null);
        black = new Node(new Node(null));
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
        expectFailure(RECURSIVE_DATASTRUCTURE, PREFAB, Node.class.getName());
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
}
