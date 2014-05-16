/*
 * Copyright 2010,2012 Jan Ouwens
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.testhelpers.types.ColorBlindColorPoint;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractClass;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.ArrayContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Interface;

import org.junit.Test;

public class InstantiatorTest {
	@Test
	public void instantiateClass() {
		Instantiator<Point> instantiator = Instantiator.of(Point.class);
		Point p = instantiator.instantiate();
		assertEquals(Point.class, p.getClass());
	}
	
	@Test
	public void fieldsOfInstantiatedObjectHaveDefaultValues() {
		ColorBlindColorPoint p = Instantiator.of(ColorBlindColorPoint.class).instantiate();
		assertEquals(0, p.x);
		assertEquals(null, p.color);
	}
	
	public void instantiateInterface() {
		Instantiator<Interface> instantiator = Instantiator.of(Interface.class);
		Interface i = instantiator.instantiate();
		assertTrue(Interface.class.isAssignableFrom(i.getClass()));
	}
	
	@Test
	public void instantiateFinalClass() {
		Instantiator.of(FinalPoint.class);
	}
	
	@Test
	public void instantiateArrayContainer() {
		Instantiator.of(ArrayContainer.class);
	}
	
	@Test
	public void instantiateAbstractClass() {
		Instantiator<AbstractClass> instantiator = Instantiator.of(AbstractClass.class);
		AbstractClass ac = instantiator.instantiate();
		assertTrue(AbstractClass.class.isAssignableFrom(ac.getClass()));
	}
	
	@Test
	public void instantiateSubclass() {
		Instantiator<Point> instantiator = Instantiator.of(Point.class);
		Point p = instantiator.instantiateAnonymousSubclass();
		assertFalse(p.getClass() == Point.class);
		assertTrue(Point.class.isAssignableFrom(p.getClass()));
	}
}
