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

import org.junit.Test;

public class PrefabEqualsVerifierTest extends EqualsVerifierTestBase {
	@Test
	public void classRecursiveFail() {
		EqualsVerifier<Node> ev = EqualsVerifier.forClass(Node.class);
		verifyFailure("Recursive datastructure. Add prefab values for one of the following classes:", ev);
	}
	
	@Test
	public void examplesRecursiveFail() {
		Node one = new Node(null);
		Node two = new Node(new Node(null));
		EqualsVerifier<Node> ev = EqualsVerifier.forExamples(one, two);
		verifyFailure("Recursive datastructure. Add prefab values for one of the following classes:", ev);
	}
	
	@Test
	public void recursiveWithPrefabValues() {
		Node one = new Node(null);
		Node two = new Node(new Node(null));
		EqualsVerifier.forClass(Node.class)
				.withPrefabValues(Node.class, one, two)
				.verify();
	}
	
	static final class Node {
		final Node node;
		
		Node(Node node) {
			this.node = node;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Node)) {
				return false;
			}
			Node other = (Node)obj;
			return node == null ? other.node == null : node.equals(other.node);
		}
		
		@Override
		public int hashCode() {
			return node == null ? 0 : 1 + node.hashCode();
		}
	}
}
