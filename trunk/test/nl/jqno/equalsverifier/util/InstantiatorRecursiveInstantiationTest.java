/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import nl.jqno.equalsverifier.util.Instantiator;

import org.junit.Test;

public class InstantiatorRecursiveInstantiationTest {
	@Test
	public void dontAddOneStepRecursiveClass() {
		Instantiator<Node> instantiator = Instantiator.forClass(Node.class);
		Node node = new Node();
		
		try {
			instantiator.scramble(node);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertEquals("Recursive datastructure. Add prefab values for one of the following classes: [class nl.jqno.equalsverifier.util.InstantiatorRecursiveInstantiationTest$Node].",
					e.getMessage());
		}
	}
	
	@Test
	public void dontAddOneStepRecursiveArrayClass() {
		Instantiator<NodeArray> instantiator = Instantiator.forClass(NodeArray.class);
		NodeArray nodeArray = new NodeArray();
		
		try {
			instantiator.scramble(nodeArray);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertEquals("Recursive datastructure. Add prefab values for one of the following classes: [class nl.jqno.equalsverifier.util.InstantiatorRecursiveInstantiationTest$NodeArray].",
					e.getMessage());
		}
	}
	
	@Test
	public void addOneStepRecursiveClass() {
		Instantiator<Node> instantiator = Instantiator.forClass(Node.class);
		instantiator.addPrefabValues(Node.class, new Node(), new Node());
		Node node = new Node();
		instantiator.scramble(node);
	}
	
	@Test
	public void addOneStepRecursiveArrayClass() {
		Instantiator<NodeArray> instantiator = Instantiator.forClass(NodeArray.class);
		instantiator.addPrefabValues(NodeArray.class, new NodeArray(), new NodeArray());
		NodeArray nodeArray = new NodeArray();
		instantiator.scramble(nodeArray);
	}
	
	@Test
	public void dontAddTwoStepRecursiveClass() {
		Instantiator<TwoStepNodeA> instantiator = Instantiator.forClass(TwoStepNodeA.class);
		TwoStepNodeA node = new TwoStepNodeA();
		
		try {
			instantiator.scramble(node);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertEquals("Recursive datastructure. Add prefab values for one of the following classes: [class nl.jqno.equalsverifier.util.InstantiatorRecursiveInstantiationTest$TwoStepNodeA, class nl.jqno.equalsverifier.util.InstantiatorRecursiveInstantiationTest$TwoStepNodeB].",
					e.getMessage());
		}
	}
	
	@Test
	public void dontAddTwoStepRecursiveArrayClass() {
		Instantiator<TwoStepNodeArrayA> instantiator = Instantiator.forClass(TwoStepNodeArrayA.class);
		TwoStepNodeArrayA node = new TwoStepNodeArrayA();
		
		try {
			instantiator.scramble(node);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertEquals("Recursive datastructure. Add prefab values for one of the following classes: [class nl.jqno.equalsverifier.util.InstantiatorRecursiveInstantiationTest$TwoStepNodeArrayA, class nl.jqno.equalsverifier.util.InstantiatorRecursiveInstantiationTest$TwoStepNodeArrayB].",
					e.getMessage());
		}
	}
	
	@Test
	public void addTwoStepRecursiveClass() {
		Instantiator<TwoStepNodeA> instantiator = Instantiator.forClass(TwoStepNodeA.class);
		instantiator.addPrefabValues(TwoStepNodeA.class, new TwoStepNodeA(), new TwoStepNodeA());
		TwoStepNodeA node = new TwoStepNodeA();
		instantiator.scramble(node);
	}
	
	@Test
	public void addTwoStepRecursiveArrayClass() {
		Instantiator<TwoStepNodeArrayA> instantiator = Instantiator.forClass(TwoStepNodeArrayA.class);
		instantiator.addPrefabValues(TwoStepNodeArrayA.class, new TwoStepNodeArrayA(), new TwoStepNodeArrayA());
		TwoStepNodeArrayA nodeArray = new TwoStepNodeArrayA();
		instantiator.scramble(nodeArray);
	}
	
	@Test
	public void sameClassButNotRecursive() {
		Instantiator<NotRecursiveA> instantiator = Instantiator.forClass(NotRecursiveA.class);
		NotRecursiveA a = instantiator.instantiate();
		instantiator.scramble(a);
	}
	
	static final class Node {
		Node node;
	}
	
	static final class NodeArray {
		NodeArray[] nodeArrays;
	}
	
	static final class TwoStepNodeA {
		TwoStepNodeB node;
	}
	
	static final class TwoStepNodeB {
		TwoStepNodeA node;
	}
	
	static final class TwoStepNodeArrayA {
		TwoStepNodeArrayB[] nodes;
	}
	
	static final class TwoStepNodeArrayB {
		TwoStepNodeArrayA[] nodes;
	}
	
	static final class NotRecursiveA {
		NotRecursiveB b;
		NotRecursiveC c;
	}
	
	static final class NotRecursiveB {
		NotRecursiveD d;
	}
	
	static final class NotRecursiveC {
		NotRecursiveD d;
	}
	
	static final class NotRecursiveD {}
}
