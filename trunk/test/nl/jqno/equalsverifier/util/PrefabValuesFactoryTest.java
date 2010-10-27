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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.NotRecursiveA;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeArrayA;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeArrayB;
import nl.jqno.equalsverifier.util.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier.util.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.util.TypeHelper.Enum;
import nl.jqno.equalsverifier.util.TypeHelper.OneElementEnum;

import org.junit.Before;
import org.junit.Test;

public class PrefabValuesFactoryTest {
	private PrefabValues prefabValues;
	private PrefabValuesFactory factory;
	
	@Before
	public void setup() {
		prefabValues = new PrefabValues();
		factory = new PrefabValuesFactory(prefabValues);
	}
	
	@Test
	public void simple() {
		factory.createFor(Point.class);
		Point red = prefabValues.getRed(Point.class);
		Point black = prefabValues.getBlack(Point.class);
		assertFalse(red.equals(black));
	}
	
	@Test
	public void createSecondTimeIsNoOp() {
		factory.createFor(Point.class);
		Point red = prefabValues.getRed(Point.class);
		Point black = prefabValues.getBlack(Point.class);
		
		factory.createFor(Point.class);
		
		assertSame(red, prefabValues.getRed(Point.class));
		assertSame(black, prefabValues.getBlack(Point.class));
	}
	
	@Test
	public void createEnum() {
		factory.createFor(Enum.class);
	}
	
	@Test(expected=InternalException.class)
	public void createOneElementEnum() {
		factory.createFor(OneElementEnum.class);
	}
	
	@Test(expected=InternalException.class)
	public void createEmptyEnum() {
		factory.createFor(EmptyEnum.class);
	}
	
	@Test
	public void oneStepRecursiveType() {
		prefabValues.put(Node.class, new Node(), new Node());
		factory.createFor(Node.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddOneStepRecursiveType() {
		factory.createFor(Node.class);
	}
	
	@Test
	public void oneStepRecursiveArrayType() {
		prefabValues.put(NodeArray.class, new NodeArray(), new NodeArray());
		factory.createFor(NodeArray.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddOneStepRecursiveArrayType() {
		factory.createFor(NodeArray.class);
	}
	
	@Test
	public void addTwoStepRecursiveType() {
		prefabValues.put(TwoStepNodeB.class, new TwoStepNodeB(), new TwoStepNodeB());
		factory.createFor(TwoStepNodeA.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddTwoStepRecursiveType() {
		factory.createFor(TwoStepNodeA.class);
	}
	
	@Test
	public void twoStepRecursiveArrayType() {
		prefabValues.put(TwoStepNodeArrayB.class, new TwoStepNodeArrayB(), new TwoStepNodeArrayB());
		factory.createFor(TwoStepNodeArrayA.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddTwoStepRecursiveArrayType() {
		factory.createFor(TwoStepNodeArrayA.class);
	}
	
	@Test
	public void sameClassTwiceButNoRecursion() {
		factory.createFor(NotRecursiveA.class);
	}
	
	@Test
	public void exceptionMessage() {
		try {
			factory.createFor(TwoStepNodeA.class);
		}
		catch (RecursionException e) {
			assertThat(e.getMessage(), containsAll(
					TwoStepNodeA.class.getSimpleName(),
					TwoStepNodeB.class.getSimpleName()));
			return;
		}
		fail("No exception thrown");
	}
}
