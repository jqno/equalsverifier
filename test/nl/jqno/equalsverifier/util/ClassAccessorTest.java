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
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllRecursiveCollectionImplementationsContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.RecursiveApiClassesContainer;
import nl.jqno.equalsverifier.testhelpers.points.PointContainer;

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
	public void getRedObject() {
		assertObjectHasNoNullFields(classAccessor.getRedObject());
	}
	
	@Test
	public void getRedAccessor() {
		PointContainer foo = classAccessor.getRedObject();
		ObjectAccessor<PointContainer> objectAccessor = classAccessor.getRedAccessor();
		assertEquals(foo, objectAccessor.get());
	}
	
	@Test
	public void getBlackObject() {
		assertObjectHasNoNullFields(classAccessor.getBlackObject());
	}

	@Test
	public void getBlackAccessor() {
		PointContainer foo = classAccessor.getBlackObject();
		ObjectAccessor<PointContainer> objectAccessor = classAccessor.getBlackAccessor();
		assertEquals(foo, objectAccessor.get());
	}

	@Test
	public void redAndBlackNotEqual() {
		PointContainer red = classAccessor.getRedObject();
		PointContainer black = classAccessor.getBlackObject();
		assertFalse(red.equals(black));
	}
	
	@Test
	public void instantiateAllTypes() {
		ClassAccessor.of(AllTypesContainer.class, prefabValues).getRedObject();
	}
	
	@Test
	public void instantiateArrayTypes() {
		ClassAccessor.of(AllArrayTypesContainer.class, prefabValues).getRedObject();
	}
	
	@Test
	public void instantiateRecursiveApiTypes() {
		ClassAccessor.of(RecursiveApiClassesContainer.class, prefabValues).getRedObject();
	}
	
	@Test
	public void instantiateCollectionImplementations() {
		ClassAccessor.of(AllRecursiveCollectionImplementationsContainer.class, prefabValues).getRedObject();
	}
	
	@Test
	public void instantiateInterfaceField() {
		ClassAccessor.of(InterfaceContainer.class, prefabValues).getRedObject();
	}
	
	@Test
	public void instantiateAbstractClassField() {
		ClassAccessor.of(AbstractClassContainer.class, prefabValues).getRedObject();
	}
	
	private void assertObjectHasNoNullFields(PointContainer foo) {
		assertNotNull(foo);
		assertNotNull(foo.getPoint());
	}
}
