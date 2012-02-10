/*
 * Copyright 2010-2012 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_CLASS_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_RUNTIME_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_CLASS_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_RUNTIME_RETENTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import nl.jqno.equalsverifier.StaticFieldValueStash;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AbstractClassContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AbstractEqualsAndHashCode;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllArrayTypesContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllRecursiveCollectionImplementationsContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AllTypesContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedFields;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithRuntime;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.InterfaceContainer;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.NoFieldsSubWithFields;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.RecursiveApiClassesContainer;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.points.PointContainer;

import org.junit.Before;
import org.junit.Test;

public class ClassAccessorTest {
	private ClassAccessor<PointContainer> classAccessor;
	private PrefabValues prefabValues;
	
	@Before
	public void setup() {
		prefabValues = new PrefabValues(new StaticFieldValueStash());
		JavaApiPrefabValues.addTo(prefabValues);
		classAccessor = ClassAccessor.of(PointContainer.class, prefabValues, false);
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
	public void hasAnnotation() {
		ClassAccessor<?> accessor = new ClassAccessor<AnnotatedWithRuntime>(AnnotatedWithRuntime.class, prefabValues, TestSupportedAnnotations.values(), false);
		assertTrue(accessor.hasAnnotation(TYPE_RUNTIME_RETENTION));
		assertFalse(accessor.hasAnnotation(TYPE_CLASS_RETENTION));
	}
	
	@Test
	public void fieldHasAnnotation() throws NoSuchFieldException {
		ClassAccessor<?> classAccessor = new ClassAccessor<AnnotatedFields>(AnnotatedFields.class, prefabValues, TestSupportedAnnotations.values(), false);
		Field field = AnnotatedFields.class.getField("runtimeRetention");
		assertTrue(classAccessor.fieldHasAnnotation(field, FIELD_RUNTIME_RETENTION));
		assertFalse(classAccessor.fieldHasAnnotation(field, FIELD_CLASS_RETENTION));
	}
	
	@Test
	public void equalsIsNotAbstract() {
		assertFalse(classAccessor.isEqualsAbstract());
	}
	
	@Test
	public void equalsIsAbstract() {
		ClassAccessor<AbstractEqualsAndHashCode> accessor = ClassAccessor.of(AbstractEqualsAndHashCode.class, prefabValues, true);
		assertTrue(accessor.isEqualsAbstract());
	}
	
	@Test
	public void hashCodeIsNotAbstract() {
		assertFalse(classAccessor.isHashCodeAbstract());
	}
	
	@Test
	public void hashCodeIsAbstract() {
		ClassAccessor<AbstractEqualsAndHashCode> accessor = ClassAccessor.of(AbstractEqualsAndHashCode.class, prefabValues, true);
		assertTrue(accessor.isHashCodeAbstract());
	}
	
	@Test
	public void equalsIsInheritedFromObject() {
		ClassAccessor<NoFieldsSubWithFields> accessor = ClassAccessor.of(NoFieldsSubWithFields.class, prefabValues, true);
		assertTrue(accessor.isEqualsInheritedFromObject());
	}
	
	@Test
	public void equalsIsNotInheritedFromObject() {
		ClassAccessor<PointContainer> accessor = ClassAccessor.of(PointContainer.class, prefabValues, true);
		assertFalse(accessor.isEqualsInheritedFromObject());
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
		ClassAccessor.of(AllTypesContainer.class, prefabValues, false).getRedObject();
	}
	
	@Test
	public void instantiateArrayTypes() {
		ClassAccessor.of(AllArrayTypesContainer.class, prefabValues, false).getRedObject();
	}
	
	@Test
	public void instantiateRecursiveApiTypes() {
		ClassAccessor.of(RecursiveApiClassesContainer.class, prefabValues, false).getRedObject();
	}
	
	@Test
	public void instantiateCollectionImplementations() {
		ClassAccessor.of(AllRecursiveCollectionImplementationsContainer.class, prefabValues, false).getRedObject();
	}
	
	@Test
	public void instantiateInterfaceField() {
		ClassAccessor.of(InterfaceContainer.class, prefabValues, false).getRedObject();
	}
	
	@Test
	public void instantiateAbstractClassField() {
		ClassAccessor.of(AbstractClassContainer.class, prefabValues, false).getRedObject();
	}
	
	private void assertObjectHasNoNullFields(PointContainer foo) {
		assertNotNull(foo);
		assertNotNull(foo.getPoint());
	}
}
