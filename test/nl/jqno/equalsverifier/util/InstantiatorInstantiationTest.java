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
package nl.jqno.equalsverifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.points.FinalPoint;
import nl.jqno.equalsverifier.points.Point;
import nl.jqno.equalsverifier.util.TypeHelper.AbstractClass;
import nl.jqno.equalsverifier.util.TypeHelper.ArrayContainer;
import nl.jqno.equalsverifier.util.TypeHelper.Interface;

import org.junit.Test;

public class InstantiatorInstantiationTest {
	@Test
	public void instantiateClass() {
		InstantiatorFacade<Point> instantiator = InstantiatorFacade.forClass(Point.class);
		Point p = instantiator.instantiate();
		assertEquals(Point.class, p.getClass());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void instantiateInterface() {
		InstantiatorFacade.forClass(Interface.class);
	}
	
	@Test
	public void instantiateFinalClass() {
		InstantiatorFacade.forClass(FinalPoint.class);
	}
	
	@Test
	public void instantiateArrayContainer() {
		InstantiatorFacade.forClass(ArrayContainer.class);
	}
	
	@Test
	public void instantiateAbstractClass() {
		InstantiatorFacade<AbstractClass> instantiator = InstantiatorFacade.forClass(AbstractClass.class);
		AbstractClass ac = instantiator.instantiate();
		assertTrue(AbstractClass.class.isAssignableFrom(ac.getClass()));
	}
	
	@Test
	public void instantiateSubclass() {
		InstantiatorFacade<Point> instantiator = InstantiatorFacade.forClass(Point.class);
		Point p = instantiator.instantiateSubclass();
		assertFalse(p.getClass() == Point.class);
		assertTrue(Point.class.isAssignableFrom(p.getClass()));
	}
}
