/*
 * Copyright 2009 Jan Ouwens
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
package nl.jqno.instantiator;

import static org.junit.Assert.assertFalse;
import nl.jqno.equalsverifier.points.FinalPoint;
import nl.jqno.equalsverifier.points.Point;
import nl.jqno.instantiator.Instantiator;

import org.junit.Test;

public class InstantiationTest {
	@Test
	public void instantiateClass() {
		Instantiator<Point> instantiator = Instantiator.forClass(Point.class);
		instantiator.instantiate();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void instantiateInterface() {
		Instantiator.forClass(Interface.class);
	}
	
	@Test
	public void instantiateFinalClass() {
		Instantiator.forClass(FinalPoint.class);
	}
	
	@Test
	public void instantiateArrayContainer() {
		Instantiator.forClass(ArrayContainer.class);
	}
	
	@Test
	public void instantiateAbstractClass() {
		Instantiator<AbstractClass> instantiator = Instantiator.forClass(AbstractClass.class);
		instantiator.instantiate();
	}
	
	@Test
	public void instantiateSubclass() {
		Instantiator<Point> instantiator = Instantiator.forClass(Point.class);
		Point p = instantiator.instantiateSubclass();
		assertFalse(p.getClass() == Point.class);
	}
	
	static abstract class AbstractClass {
		public int value = 42; 
	}
	
	static interface Interface {}
	
	static class ArrayContainer {
		public int[] array;
	}
}
