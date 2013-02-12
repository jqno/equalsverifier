/*
 * Copyright 2010-2013 Jan Ouwens
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

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import nl.jqno.equalsverifier.testhelpers.MockStaticFieldValueStash;
import nl.jqno.equalsverifier.testhelpers.Util;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.Node;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.NodeArray;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.NotRecursiveA;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.RecursiveThisIsTheOtherField;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.RecursiveWithAnotherFieldFirst;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.TwoStepNodeA;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.TwoStepNodeArrayA;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.TwoStepNodeArrayB;
import nl.jqno.equalsverifier.testhelpers.RecursiveTypeHelper.TwoStepNodeB;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.EmptyEnum;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.Enum;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.OneElementEnum;
import nl.jqno.equalsverifier.testhelpers.points.Point;

import org.junit.Before;
import org.junit.Test;

public class PrefabValuesCreatorTest {
	private MockStaticFieldValueStash stash;
	private PrefabValues prefabValues;
	
	@Before
	public void setup() {
		stash = new MockStaticFieldValueStash();
		prefabValues = new PrefabValues(stash);
	}
	
	@Test
	public void stashed() {
		prefabValues.putFor(Point.class);
		assertEquals(Point.class, stash.lastBackuppedType);
	}
	
	@Test
	public void simple() {
		prefabValues.putFor(Point.class);
		Point red = prefabValues.getRed(Point.class);
		Point black = prefabValues.getBlack(Point.class);
		assertFalse(red.equals(black));
	}
	
	@Test
	public void createSecondTimeIsNoOp() {
		prefabValues.putFor(Point.class);
		Point red = prefabValues.getRed(Point.class);
		Point black = prefabValues.getBlack(Point.class);
		
		prefabValues.putFor(Point.class);
		
		assertSame(red, prefabValues.getRed(Point.class));
		assertSame(black, prefabValues.getBlack(Point.class));
	}
	
	@Test
	public void createEnum() {
		prefabValues.putFor(Enum.class);
	}
	
	@Test(expected=InternalException.class)
	public void createOneElementEnum() {
		prefabValues.putFor(OneElementEnum.class);
	}
	
	@Test(expected=InternalException.class)
	public void createEmptyEnum() {
		prefabValues.putFor(EmptyEnum.class);
	}
	
	@Test
	public void oneStepRecursiveType() {
		prefabValues.put(Node.class, new Node(), new Node());
		prefabValues.putFor(Node.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddOneStepRecursiveType() {
		prefabValues.putFor(Node.class);
	}
	
	@Test
	public void oneStepRecursiveArrayType() {
		prefabValues.put(NodeArray.class, new NodeArray(), new NodeArray());
		prefabValues.putFor(NodeArray.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddOneStepRecursiveArrayType() {
		prefabValues.putFor(NodeArray.class);
	}
	
	@Test
	public void addTwoStepRecursiveType() {
		prefabValues.put(TwoStepNodeB.class, new TwoStepNodeB(), new TwoStepNodeB());
		prefabValues.putFor(TwoStepNodeA.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddTwoStepRecursiveType() {
		prefabValues.putFor(TwoStepNodeA.class);
	}
	
	@Test
	public void twoStepRecursiveArrayType() {
		prefabValues.put(TwoStepNodeArrayB.class, new TwoStepNodeArrayB(), new TwoStepNodeArrayB());
		prefabValues.putFor(TwoStepNodeArrayA.class);
	}
	
	@Test(expected=RecursionException.class)
	public void dontAddTwoStepRecursiveArrayType() {
		prefabValues.putFor(TwoStepNodeArrayA.class);
	}
	
	@Test
	public void sameClassTwiceButNoRecursion() {
		prefabValues.putFor(NotRecursiveA.class);
	}
	
	@Test
	public void recursiveWithAnotherFieldFirst() {
		try {
			prefabValues.putFor(RecursiveWithAnotherFieldFirst.class);
		}
		catch (Exception e) {
			assertThat(e.getMessage(), containsString(RecursiveWithAnotherFieldFirst.class.getSimpleName()));
			assertThat(e.getMessage(), not(containsString(RecursiveThisIsTheOtherField.class.getSimpleName())));
			return;
		}
		fail("No exception thrown");
	}
	
	@Test
	public void exceptionMessage() {
		try {
			prefabValues.putFor(TwoStepNodeA.class);
		}
		catch (RecursionException e) {
			Util.assertMessageContainsAll(e,
					TwoStepNodeA.class.getSimpleName(),
					TwoStepNodeB.class.getSimpleName());
			return;
		}
		fail("No exception thrown");
	}
	
	@Test
	public void skipStaticFinal() {
		prefabValues.putFor(StaticFinalContainer.class);
	}
	
	static class StaticFinalContainer {
		public static final StaticFinalContainer x = new StaticFinalContainer();
	}
}
