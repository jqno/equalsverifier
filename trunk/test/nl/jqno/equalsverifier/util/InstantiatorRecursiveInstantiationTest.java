/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

import static nl.jqno.equalsverifier.Helper.containsAll;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.NotRecursiveA;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeArrayA;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeArrayB;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeB;

import org.junit.Test;

public class InstantiatorRecursiveInstantiationTest {
	@Test
	public void dontAddOneStepRecursiveClass() {
		InstantiatorFacade<Node> instantiator = InstantiatorFacade.forClass(Node.class);
		Node node = new Node();
		
		try {
			instantiator.scramble(node);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertThat(e.getMessage(), containsString(Node.class.getName()));
		}
	}
	
	@Test
	public void dontAddOneStepRecursiveArrayClass() {
		InstantiatorFacade<NodeArray> instantiator = InstantiatorFacade.forClass(NodeArray.class);
		NodeArray nodeArray = new NodeArray();
		
		try {
			instantiator.scramble(nodeArray);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertThat(e.getMessage(), containsString(NodeArray.class.getName()));
		}
	}
	
	@Test
	public void addOneStepRecursiveClass() {
		InstantiatorFacade<Node> instantiator = InstantiatorFacade.forClass(Node.class);
		instantiator.addPrefabValues(Node.class, new Node(), new Node());
		Node node = new Node();
		instantiator.scramble(node);
	}
	
	@Test
	public void addOneStepRecursiveArrayClass() {
		InstantiatorFacade<NodeArray> instantiator = InstantiatorFacade.forClass(NodeArray.class);
		instantiator.addPrefabValues(NodeArray.class, new NodeArray(), new NodeArray());
		NodeArray nodeArray = new NodeArray();
		instantiator.scramble(nodeArray);
	}
	
	@Test
	public void dontAddTwoStepRecursiveClass() {
		InstantiatorFacade<TwoStepNodeA> instantiator = InstantiatorFacade.forClass(TwoStepNodeA.class);
		TwoStepNodeA node = new TwoStepNodeA();
		
		try {
			instantiator.scramble(node);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertThat(e.getMessage(), containsAll(TwoStepNodeA.class.getName(), TwoStepNodeB.class.getName()));
		}
	}
	
	@Test
	public void dontAddTwoStepRecursiveArrayClass() {
		InstantiatorFacade<TwoStepNodeArrayA> instantiator = InstantiatorFacade.forClass(TwoStepNodeArrayA.class);
		TwoStepNodeArrayA node = new TwoStepNodeArrayA();
		
		try {
			instantiator.scramble(node);
			fail("No exception thrown.");
		}
		catch (AssertionError e) {
			assertThat(e.getMessage(), containsAll(TwoStepNodeArrayA.class.getName(), TwoStepNodeArrayB.class.getName()));
		}
	}
	
	@Test
	public void addTwoStepRecursiveClass() {
		InstantiatorFacade<TwoStepNodeA> instantiator = InstantiatorFacade.forClass(TwoStepNodeA.class);
		instantiator.addPrefabValues(TwoStepNodeA.class, new TwoStepNodeA(), new TwoStepNodeA());
		TwoStepNodeA node = new TwoStepNodeA();
		instantiator.scramble(node);
	}
	
	@Test
	public void addTwoStepRecursiveArrayClass() {
		InstantiatorFacade<TwoStepNodeArrayA> instantiator = InstantiatorFacade.forClass(TwoStepNodeArrayA.class);
		instantiator.addPrefabValues(TwoStepNodeArrayA.class, new TwoStepNodeArrayA(), new TwoStepNodeArrayA());
		TwoStepNodeArrayA nodeArray = new TwoStepNodeArrayA();
		instantiator.scramble(nodeArray);
	}
	
	@Test
	public void sameClassButNotRecursive() {
		InstantiatorFacade<NotRecursiveA> instantiator = InstantiatorFacade.forClass(NotRecursiveA.class);
		NotRecursiveA a = instantiator.instantiate();
		instantiator.scramble(a);
	}
}
