/*
 * Copyright 2009 Jan Ouwens
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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.Helper.assertFailure;
import static nl.jqno.equalsverifier.Helper.nullSafeEquals;
import static nl.jqno.equalsverifier.Helper.nullSafeHashCode;

import org.junit.Before;
import org.junit.Test;

public class RecursionTest {
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
	public void classRecursiveFail() {
		EqualsVerifier<Node> ev = EqualsVerifier.forClass(Node.class);
		assertFailure(ev, RECURSIVE_DATASTRUCTURE, PREFAB);
	}
	
	@Test
	public void examplesRecursiveFail() {
		EqualsVerifier<Node> ev = EqualsVerifier.forExamples(red, black);
		assertFailure(ev, RECURSIVE_DATASTRUCTURE, PREFAB);
	}
	
	@Test
	public void recursiveWithPrefabValues() {
		EqualsVerifier.forClass(Node.class)
				.withPrefabValues(Node.class, red, black)
				.verify();
	}
	
	@Test
	public void recursiveWithPrefabValuesForSuper() {
		EqualsVerifier.forClass(SubNode.class)
				.withPrefabValues(Node.class, red, black)
				.verify();
	}
	
	@Test
	public void recursiveClassContainerWithPrefabValues() {
		EqualsVerifier.forClass(NodeContainer.class)
				.withPrefabValues(Node.class, red, black)
				.verify();
	}
	
	@Test
	public void recursiveClassContainerWithPrefabValuesForSuper() {
		EqualsVerifier.forClass(SubNodeContainer.class)
				.withPrefabValues(Node.class, red, black)
				.verify();
	}
	
	static class Node {
		final Node node;
		
		Node(Node node) {
			this.node = node;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof Node)) {
				return false;
			}
			Node other = (Node)obj;
			return nullSafeEquals(node, other.node);
		}
		
		@Override
		public final int hashCode() {
			return node == null ? 0 : 1 + node.hashCode();
		}
	}
	
	static class SubNode extends Node {
		SubNode(Node node) {
			super(node);
		}
	}
	
	static class NodeContainer {
		final Node node;
		
		NodeContainer(Node node) {
			this.node = node;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof NodeContainer)) {
				return false;
			}
			NodeContainer other = (NodeContainer)obj;
			return nullSafeEquals(node, other.node);
		}
		
		@Override
		public final int hashCode() {
			return nullSafeHashCode(node);
		}
	}
	
	static class SubNodeContainer extends NodeContainer {
		public SubNodeContainer(Node node) {
			super(node);
		}
	}
}
