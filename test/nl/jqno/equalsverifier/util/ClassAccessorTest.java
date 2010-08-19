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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import nl.jqno.equalsverifier.points.PointContainer;

import org.junit.Before;
import org.junit.Test;

public class ClassAccessorTest {
	private ClassAccessor<PointContainer> classAccessor;
	private PrefabValues prefabValues;
	
	@Before
	public void setup() {
		prefabValues = PrefabValuesFactory.withJavaClasses();
		classAccessor = ClassAccessor.of(PointContainer.class, prefabValues);
	}
	
	@Test
	public void getType() {
		assertSame(PointContainer.class, classAccessor.getType());
	}
	
	@Test
	public void getPrefabValues() {
		assertSame(prefabValues, classAccessor.getPrefabValues());
	}
	
	@Test
	public void getFirstObject() {
		assertObjectHasNoNullFields(classAccessor.getFirstObject());
	}
	
	@Test
	public void getFirstAccessor() {
		PointContainer foo = classAccessor.getFirstObject();
		ObjectAccessor<PointContainer> objectAccessor = classAccessor.getFirstAccessor();
		assertEquals(foo, objectAccessor.get());
	}
	
	@Test
	public void getSecondObject() {
		assertObjectHasNoNullFields(classAccessor.getSecondObject());
	}

	@Test
	public void getSecondAccessor() {
		PointContainer foo = classAccessor.getSecondObject();
		ObjectAccessor<PointContainer> objectAccessor = classAccessor.getSecondAccessor();
		assertEquals(foo, objectAccessor.get());
	}

	@Test
	public void firstAndSecondNotEqual() {
		PointContainer first = classAccessor.getFirstObject();
		PointContainer second = classAccessor.getSecondObject();
		assertFalse(first.equals(second));
		
	}

	private void assertObjectHasNoNullFields(PointContainer foo) {
		assertNotNull(foo);
		assertNotNull(foo.getPoint());
	}
}
