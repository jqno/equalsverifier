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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.util.InstantiatorFacade;
import nl.jqno.equalsverifier.util.TypeHelper.AbstractAndInterfaceArrayContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.util.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.util.TypeHelper.ObjectContainer;
import nl.jqno.equalsverifier.util.TypeHelper.PrimitiveContainer;
import nl.jqno.equalsverifier.util.TypeHelper.StaticFinalContainer;

import org.junit.Test;

public class InstantiatorFieldModificationTest {
	@Test
	public void nullField() throws SecurityException, NoSuchFieldException {
		Class<ObjectContainer> klass = ObjectContainer.class;
		InstantiatorFacade<ObjectContainer> instantiator = InstantiatorFacade.forClass(klass);
		
		ObjectContainer oc = new ObjectContainer();
		instantiator.nullField(klass.getDeclaredField("_object"), oc);
		
		assertNull(oc._object);
	}
	
	@Test
	public void nullFieldPrimitive() throws SecurityException, NoSuchFieldException {
		Class<PrimitiveContainer> klass = PrimitiveContainer.class;
		InstantiatorFacade<PrimitiveContainer> instantiator = InstantiatorFacade.forClass(klass);
		
		PrimitiveContainer pc = new PrimitiveContainer();
		pc.i = 10;
		instantiator.nullField(klass.getDeclaredField("i"), pc);
		
		assertEquals(10, pc.i);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void nullFieldStaticFinal() throws SecurityException, NoSuchFieldException {
		Class<StaticFinalContainer> klass = StaticFinalContainer.class;
		InstantiatorFacade<StaticFinalContainer> instantiator = InstantiatorFacade.forClass(klass);
		StaticFinalContainer sfc = new StaticFinalContainer();
		
		instantiator.nullField(klass.getDeclaredField("CONST"), sfc);
		assertEquals(42, sfc.CONST);
		
		Object original = sfc.OBJECT;
		instantiator.nullField(klass.getDeclaredField("OBJECT"), sfc);
		assertEquals(original, sfc.OBJECT);
	}
	
	@Test
	public void changeField() {
		InstantiatorFacade<AllTypesContainer> instantiator = InstantiatorFacade.forClass(AllTypesContainer.class);
		AllTypesContainer reference = new AllTypesContainer();
		AllTypesContainer changed = new AllTypesContainer();
		
		assertEquals(reference, changed);
		for (Field field : AllTypesContainer.class.getDeclaredFields()) {
			if (InstantiatorFacade.canBeModifiedReflectively(field)) {
				instantiator.changeField(field, changed);
				assertFalse(reference.equals(changed));
				instantiator.changeField(field, reference);
				assertEquals(reference, changed);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void changeFieldStaticFinal() throws SecurityException, NoSuchFieldException {
		Class<StaticFinalContainer> klass = StaticFinalContainer.class;
		InstantiatorFacade<StaticFinalContainer> instantiator = InstantiatorFacade.forClass(klass);
		StaticFinalContainer sfc = new StaticFinalContainer();
		
		instantiator.changeField(klass.getDeclaredField("CONST"), sfc);
		assertEquals(42, sfc.CONST);

		Object original = sfc.OBJECT;
		instantiator.changeField(klass.getDeclaredField("OBJECT"), sfc);
		assertEquals(original, sfc.OBJECT);
	}
	
	@Test
	public void changeFieldAbstract() throws SecurityException, NoSuchFieldException {
		Class<AbstractClassContainer> klass = AbstractClassContainer.class;
		InstantiatorFacade<AbstractClassContainer> instantiator = InstantiatorFacade.forClass(klass);
		
		AbstractClassContainer acc = new AbstractClassContainer();
		instantiator.changeField(klass.getDeclaredField("ac"), acc);
		assertNotNull(acc.ac);
	}
	
	@Test
	public void changeFieldInterface() throws SecurityException, NoSuchFieldException {
		Class<InterfaceContainer> klass = InterfaceContainer.class;
		InstantiatorFacade<InterfaceContainer> instantiator = InstantiatorFacade.forClass(klass);
		
		InterfaceContainer ic = new InterfaceContainer();
		instantiator.changeField(klass.getDeclaredField("_interface"), ic);
		assertNotNull(ic._interface);
	}
	
	@Test(expected=InternalException.class)
	public void changeNotArrayElement() {
		Object o = new Object();
		InstantiatorFacade<Object> instantiator = InstantiatorFacade.forClass(Object.class);
		instantiator.changeArrayElement(o, 0);
	}
	
	@Test
	public void changeArrayElement() throws IllegalArgumentException, IllegalAccessException {
		InstantiatorFacade<AllArrayTypesContainer> instantiator = InstantiatorFacade.forClass(AllArrayTypesContainer.class);
		AllArrayTypesContainer reference = new AllArrayTypesContainer();
		AllArrayTypesContainer changed = new AllArrayTypesContainer();
		
		assertEquals(reference, changed);
		for (Field field : AllArrayTypesContainer.class.getDeclaredFields()) {
			if (InstantiatorFacade.canBeModifiedReflectively(field)) {
				instantiator.changeArrayElement(field.get(changed), 0);
				assertFalse(reference.equals(changed));
				instantiator.changeArrayElement(field.get(reference), 0);
				assertEquals(reference, changed);
			}
		}
	}
	
	@Test
	public void changeAbstractArrayElement() {
		InstantiatorFacade<AbstractAndInterfaceArrayContainer> instantiator = InstantiatorFacade.forClass(AbstractAndInterfaceArrayContainer.class);
		AbstractAndInterfaceArrayContainer aac = new AbstractAndInterfaceArrayContainer();
		instantiator.changeArrayElement(aac.abstractClasses, 0);
		assertNotNull(aac.abstractClasses[0]);
	}
	
	@Test
	public void changeInterfaceArrayElement() {
		InstantiatorFacade<AbstractAndInterfaceArrayContainer> instantiator = InstantiatorFacade.forClass(AbstractAndInterfaceArrayContainer.class);
		AbstractAndInterfaceArrayContainer aac = new AbstractAndInterfaceArrayContainer();
		instantiator.changeArrayElement(aac.interfaces, 0);
		assertNotNull(aac.interfaces[0]);
	}
}
