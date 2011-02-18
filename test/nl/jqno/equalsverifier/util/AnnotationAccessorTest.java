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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedFields;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithBoth;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithClass;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.AnnotatedWithRuntime;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.SubclassOfAnnotatedFields;
import nl.jqno.equalsverifier.testhelpers.TypeHelper.SubclassOfAnnotatedWithBoth;
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationRuntimeRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.TypeAnnotationRuntimeRetention;

import org.junit.Test;

public class AnnotationAccessorTest {
	private static final String TYPE_ANNOTATION_RUNTIME_RETENTION = "Lnl/jqno/equalsverifier/testhelpers/annotations/TypeAnnotationRuntimeRetention;";
	private static final String TYPE_ANNOTATION_CLASS_RETENTION = "Lnl/jqno/equalsverifier/testhelpers/annotations/TypeAnnotationClassRetention;";
	private static final String FIELD_ANNOTATION_RUNTIME_RETENTION = "Lnl/jqno/equalsverifier/testhelpers/annotations/FieldAnnotationRuntimeRetention;";
	private static final String FIELD_ANNOTATION_CLASS_RETENTION = "Lnl/jqno/equalsverifier/testhelpers/annotations/FieldAnnotationClassRetention;";
	
	private static final String RUNTIME_RETENTION = "runtimeRetention";
	private static final String CLASS_RETENTION = "classRetention";
	private static final String BOTH_RETENTIONS = "bothRetentions";
	private static final String NO_RETENTION = "noRetention";

	@Test
	public void findRuntimeAnnotationInType() {
		assertTypeHasAnnotation(AnnotatedWithRuntime.class, TYPE_ANNOTATION_RUNTIME_RETENTION);
		assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_ANNOTATION_RUNTIME_RETENTION);
		
		assertTypeDoesNotHaveAnnotation(AnnotatedWithClass.class, TYPE_ANNOTATION_RUNTIME_RETENTION);
		assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_ANNOTATION_RUNTIME_RETENTION);
	}

	@Test
	public void findClassAnnotationInType() {
		assertTypeHasAnnotation(AnnotatedWithClass.class, TYPE_ANNOTATION_CLASS_RETENTION);
		assertTypeHasAnnotation(AnnotatedWithBoth.class, TYPE_ANNOTATION_CLASS_RETENTION);
		
		assertTypeDoesNotHaveAnnotation(AnnotatedWithRuntime.class, TYPE_ANNOTATION_CLASS_RETENTION);
		assertTypeDoesNotHaveAnnotation(AnnotatedFields.class, TYPE_ANNOTATION_CLASS_RETENTION);
	}

	@Test
	public void findRuntimeAnnotationInField() {
		assertFieldHasAnnotation(RUNTIME_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION);
		assertFieldHasAnnotation(BOTH_RETENTIONS, FIELD_ANNOTATION_RUNTIME_RETENTION);
	
		assertFieldDoesNotHaveAnnotation(CLASS_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION);
		assertFieldDoesNotHaveAnnotation(NO_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION);
	}

	@Test
	public void findClassAnnotationInField() {
		assertFieldHasAnnotation(CLASS_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION);
		assertFieldHasAnnotation(BOTH_RETENTIONS, FIELD_ANNOTATION_CLASS_RETENTION);
	
		assertFieldDoesNotHaveAnnotation(RUNTIME_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION);
		assertFieldDoesNotHaveAnnotation(NO_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION);
	}
	
	@Test
	public void findPartialAnnotationName() {
		assertTypeHasAnnotation(AnnotatedWithRuntime.class, "TypeAnnotationRuntimeRetention");
		assertFieldHasAnnotation(RUNTIME_RETENTION, "FieldAnnotationRuntimeRetention");
	}
	
	@Test
	public void findFullyQualifiedAnnotationName() {
		String canonicalTypeAnnotationName = TypeAnnotationRuntimeRetention.class.getCanonicalName();
		assertTypeHasAnnotation(AnnotatedWithRuntime.class, canonicalTypeAnnotationName);
		
		String canonicalFieldAnnotationName = FieldAnnotationRuntimeRetention.class.getCanonicalName();
		assertFieldHasAnnotation(RUNTIME_RETENTION, canonicalFieldAnnotationName);
	}
	
	@Test(expected=InternalException.class)
	public void searchNonExistingField() {
		findFieldAnnotationFor(AnnotatedFields.class, "x", "");
	}
	
	@Test
	public void findClassAnnotationsInSuperclass() {
		assertTypeHasAnnotation(SubclassOfAnnotatedWithBoth.class, TYPE_ANNOTATION_RUNTIME_RETENTION);
		assertTypeHasAnnotation(SubclassOfAnnotatedWithBoth.class, TYPE_ANNOTATION_CLASS_RETENTION);
	}
	
	@Test
	public void findFieldAnnotationsInSuperclass() {
		assertFieldHasAnnotation(SubclassOfAnnotatedFields.class, RUNTIME_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION);
		assertFieldHasAnnotation(SubclassOfAnnotatedFields.class, CLASS_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION);
	}
	
	private void assertTypeHasAnnotation(Class<?> type, String annotationDescriptor) {
		assertTrue(findTypeAnnotationFor(type, annotationDescriptor));
	}
	
	private void assertTypeDoesNotHaveAnnotation(Class<?> type, String annotationDescriptor) {
		assertFalse(findTypeAnnotationFor(type, annotationDescriptor));
	}
	
	private void assertFieldHasAnnotation(String fieldName, String annotationDescriptor) {
		assertFieldHasAnnotation(AnnotatedFields.class, fieldName, annotationDescriptor);
	}
	
	private void assertFieldHasAnnotation(Class<?> type, String fieldName, String annotationDescriptor) {
		assertTrue(findFieldAnnotationFor(type, fieldName, annotationDescriptor));
	}
	
	private void assertFieldDoesNotHaveAnnotation(String fieldName, String annotationDescriptor) {
		assertFalse(findFieldAnnotationFor(AnnotatedFields.class, fieldName, annotationDescriptor));
	}
	
	private boolean findTypeAnnotationFor(Class<?> type, String annotationDescriptor) {
		AnnotationAccessor accessor = new AnnotationAccessor(type);
		return accessor.typeHas(annotationDescriptor);
	}
	
	private boolean findFieldAnnotationFor(Class<?> type, String fieldName, String annotationDescriptor) {
		AnnotationAccessor accessor = new AnnotationAccessor(type);
		return accessor.fieldHas(fieldName, annotationDescriptor);
	}
}
