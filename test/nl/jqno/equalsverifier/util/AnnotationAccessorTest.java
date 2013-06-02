/*
 * Copyright 2011 Jan Ouwens
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
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_DOESNT_INHERIT;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_INHERITS;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_RUNTIME_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.FIELD_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_CLASS_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_DOESNT_INHERIT;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_INHERITS;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_RUNTIME_RETENTION;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR;
import static nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations.TYPE_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedFields;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithBoth;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithClass;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithRuntime;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.LoadedBySystemClassLoader;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.SubclassWithAnnotations;
import nl.jqno.equalsverifier.testhelpers.annotations.TestSupportedAnnotations;
import nl.jqno.equalsverifier.testhelpers.points.Point;
import nl.jqno.equalsverifier.util.exceptions.ReflectionException;

import org.junit.Test;

public class AnnotationAccessorTest {
	private static final String RUNTIME_RETENTION = "runtimeRetention";
	private static final String CLASS_RETENTION = "classRetention";
	private static final String BOTH_RETENTIONS = "bothRetentions";
	private static final String NO_RETENTION = "noRetention";

	@Test
	public void loadedBySystemClassLoaderDoesNotThrowNullPointerException() {
		AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), LoadedBySystemClassLoader.class, false);
		accessor.typeHas(null);
	}

	@Test
	public void findRuntimeAnnotationInType() {
		assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION);
		assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_RUNTIME_RETENTION);
		
		assertTypeDoesNotHaveAnnotation(AnnotatedWithClass.class, TYPE_RUNTIME_RETENTION);
		assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_RUNTIME_RETENTION);
	}

	@Test
	public void findClassAnnotationInType() {
		assertTypeHasAnnotation(AnnotatedWithClass.class, TYPE_CLASS_RETENTION);
		assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_CLASS_RETENTION);
		
		assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_CLASS_RETENTION);
		assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_CLASS_RETENTION);
	}

	@Test
	public void findRuntimeAnnotationInField() {
		assertFieldHasAnnotation(RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION);
		assertFieldHasAnnotation(BOTH_RETENTIONS, FIELD_RUNTIME_RETENTION);
	
		assertFieldDoesNotHaveAnnotation(CLASS_RETENTION, FIELD_RUNTIME_RETENTION);
		assertFieldDoesNotHaveAnnotation(NO_RETENTION, FIELD_RUNTIME_RETENTION);
	}

	@Test
	public void findClassAnnotationInField() {
		assertFieldHasAnnotation(CLASS_RETENTION, FIELD_CLASS_RETENTION);
		assertFieldHasAnnotation(BOTH_RETENTIONS, FIELD_CLASS_RETENTION);
	
		assertFieldDoesNotHaveAnnotation(RUNTIME_RETENTION, FIELD_CLASS_RETENTION);
		assertFieldDoesNotHaveAnnotation(NO_RETENTION, FIELD_CLASS_RETENTION);
	}
	
	@Test
	public void findPartialAnnotationName() {
		assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR);
		assertFieldHasAnnotation(RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR);
	}
	
	@Test
	public void findFullyQualifiedAnnotationName() {
		assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR);
		assertFieldHasAnnotation(RUNTIME_RETENTION, FIELD_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR);
	}
	
	@Test(expected=ReflectionException.class)
	public void searchNonExistingField() {
		findFieldAnnotationFor(AnnotatedFields.class, "x", FIELD_RUNTIME_RETENTION);
	}
	
	@Test
	public void typeAnnotationInheritance() {
		assertTypeHasAnnotation(SubclassWithAnnotations.class, TYPE_INHERITS);
		assertTypeDoesNotHaveAnnotation(SubclassWithAnnotations.class, TYPE_DOESNT_INHERIT);
	}
	
	@Test
	public void fieldAnnotationInheritance() {
		assertFieldHasAnnotation(SubclassWithAnnotations.class, "inherits", FIELD_INHERITS);
		assertFieldDoesNotHaveAnnotation(SubclassWithAnnotations.class, "doesntInherit", FIELD_DOESNT_INHERIT);
	}
	
	@Test(expected=ReflectionException.class)
	public void dynamicClassThrowsException() {
		Class<?> type = Instantiator.of(Point.class).instantiateAnonymousSubclass().getClass();
		AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, false);
		accessor.typeHas(TYPE_CLASS_RETENTION);
	}
	
	@Test
	public void dynamicClassWithSuppressedWarning() {
		Class<?> type = Instantiator.of(Point.class).instantiateAnonymousSubclass().getClass();
		AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, true);
		assertFalse(accessor.typeHas(TYPE_CLASS_RETENTION));
	}
	
	@Test
	public void regularClassWithSuppressedWarningStillProcessesAnnotation() {
		AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), AnnotatedWithClass.class, true);
		assertTrue(accessor.typeHas(TYPE_CLASS_RETENTION));
	}
	
	private void assertTypeHasAnnotation(Class<?> type, Annotation annotation) {
		assertTrue(findTypeAnnotationFor(type, annotation));
	}
	
	private void assertTypeDoesNotHaveAnnotation(Class<?> type, Annotation annotation) {
		assertFalse(findTypeAnnotationFor(type, annotation));
	}
	
	private void assertFieldHasAnnotation(String fieldName, Annotation annotation) {
		assertFieldHasAnnotation(AnnotatedFields.class, fieldName, annotation);
	}
	
	private void assertFieldHasAnnotation(Class<?> type, String fieldName, Annotation annotation) {
		assertTrue(findFieldAnnotationFor(type, fieldName, annotation));
	}
	
	private void assertFieldDoesNotHaveAnnotation(String fieldName, Annotation annotation) {
		assertFieldDoesNotHaveAnnotation(AnnotatedFields.class, fieldName, annotation);
	}
	
	private void assertFieldDoesNotHaveAnnotation(Class<?> type, String fieldName, Annotation annotation) {
		assertFalse(findFieldAnnotationFor(type, fieldName, annotation));
	}
	
	private boolean findTypeAnnotationFor(Class<?> type, Annotation annotation) {
		AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, false);
		return accessor.typeHas(annotation);
	}
	
	private boolean findFieldAnnotationFor(Class<?> type, String fieldName, Annotation annotation) {
		AnnotationAccessor accessor = new AnnotationAccessor(TestSupportedAnnotations.values(), type, false);
		return accessor.fieldHas(fieldName, annotation);
	}
}
