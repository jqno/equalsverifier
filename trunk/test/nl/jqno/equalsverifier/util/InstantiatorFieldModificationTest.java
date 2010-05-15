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

import nl.jqno.equalsverifier.util.Instantiator;
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
		Instantiator<ObjectContainer> instantiator = Instantiator.forClass(klass);
		
		ObjectContainer oc = new ObjectContainer();
		instantiator.nullField(klass.getField("_object"), oc);
		
		assertNull(oc._object);
	}
	
	@Test
	public void nullFieldPrimitive() throws SecurityException, NoSuchFieldException {
		Class<PrimitiveContainer> klass = PrimitiveContainer.class;
		Instantiator<PrimitiveContainer> instantiator = Instantiator.forClass(klass);
		
		PrimitiveContainer pc = new PrimitiveContainer();
		instantiator.nullField(klass.getField("i"), pc);
		
		assertEquals(10, pc.i);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void nullFieldStaticFinal() throws SecurityException, NoSuchFieldException {
		Class<StaticFinalContainer> klass = StaticFinalContainer.class;
		Instantiator<StaticFinalContainer> instantiator = Instantiator.forClass(klass);
		StaticFinalContainer sfc = new StaticFinalContainer();
		
		instantiator.nullField(klass.getField("CONST"), sfc);
		assertEquals(42, sfc.CONST);
		
		Object original = sfc.OBJECT;
		instantiator.nullField(klass.getField("OBJECT"), sfc);
		assertEquals(original, sfc.OBJECT);
	}
	
	@Test
	public void changeField() {
		Instantiator<AllTypesContainer> instantiator = Instantiator.forClass(AllTypesContainer.class);
		AllTypesContainer reference = new AllTypesContainer();
		AllTypesContainer changed = new AllTypesContainer();
		
		assertEquals(reference, changed);
		for (Field field : AllTypesContainer.class.getDeclaredFields()) {
			if (Instantiator.canBeModifiedReflectively(field)) {
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
		Instantiator<StaticFinalContainer> instantiator = Instantiator.forClass(klass);
		StaticFinalContainer sfc = new StaticFinalContainer();
		
		instantiator.changeField(klass.getField("CONST"), sfc);
		assertEquals(42, sfc.CONST);

		Object original = sfc.OBJECT;
		instantiator.changeField(klass.getField("OBJECT"), sfc);
		assertEquals(original, sfc.OBJECT);
	}
	
	@Test
	public void changeFieldAbstract() throws SecurityException, NoSuchFieldException {
		Class<AbstractClassContainer> klass = AbstractClassContainer.class;
		Instantiator<AbstractClassContainer> instantiator = Instantiator.forClass(klass);
		
		AbstractClassContainer acc = new AbstractClassContainer();
		instantiator.changeField(klass.getField("ac"), acc);
		assertNotNull(acc.ac);
	}
	
	@Test
	public void changeFieldInterface() throws SecurityException, NoSuchFieldException {
		Class<InterfaceContainer> klass = InterfaceContainer.class;
		Instantiator<InterfaceContainer> instantiator = Instantiator.forClass(klass);
		
		InterfaceContainer ic = new InterfaceContainer();
		instantiator.changeField(klass.getField("_interface"), ic);
		assertNotNull(ic._interface);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void changeNotArrayElement() {
		Object o = new Object();
		Instantiator<Object> instantiator = Instantiator.forClass(Object.class);
		instantiator.changeArrayElement(o, 0);
	}
	
	@Test
	public void changeArrayElement() throws IllegalArgumentException, IllegalAccessException {
		Instantiator<AllArrayTypesContainer> instantiator = Instantiator.forClass(AllArrayTypesContainer.class);
		AllArrayTypesContainer reference = new AllArrayTypesContainer();
		AllArrayTypesContainer changed = new AllArrayTypesContainer();
		
		assertEquals(reference, changed);
		for (Field field : AllArrayTypesContainer.class.getDeclaredFields()) {
			if (Instantiator.canBeModifiedReflectively(field)) {
				instantiator.changeArrayElement(field.get(changed), 0);
				assertFalse(reference.equals(changed));
				instantiator.changeArrayElement(field.get(reference), 0);
				assertEquals(reference, changed);
			}
		}
	}
	
	@Test
	public void changeAbstractArrayElement() {
		Instantiator<AbstractAndInterfaceArrayContainer> instantiator = Instantiator.forClass(AbstractAndInterfaceArrayContainer.class);
		AbstractAndInterfaceArrayContainer aac = new AbstractAndInterfaceArrayContainer();
		instantiator.changeArrayElement(aac.abstractClasses, 0);
		assertNotNull(aac.abstractClasses[0]);
	}
	
	@Test
	public void changeInterfaceArrayElement() {
		Instantiator<AbstractAndInterfaceArrayContainer> instantiator = Instantiator.forClass(AbstractAndInterfaceArrayContainer.class);
		AbstractAndInterfaceArrayContainer aac = new AbstractAndInterfaceArrayContainer();
		instantiator.changeArrayElement(aac.interfaces, 0);
		assertNotNull(aac.interfaces[0]);
	}
}
