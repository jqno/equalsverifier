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
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationClassRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.FieldAnnotationRuntimeRetention;
import nl.jqno.equalsverifier.testhelpers.annotations.TypeAnnotationClassRetention;
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
		assertTrue(findTypeAnnotationFor(AnnotatedWithRuntime.class, TYPE_ANNOTATION_RUNTIME_RETENTION));
		assertTrue(findTypeAnnotationFor(AnnotatedWithBoth.class, TYPE_ANNOTATION_RUNTIME_RETENTION));
		
		assertFalse(findTypeAnnotationFor(AnnotatedWithClass.class, TYPE_ANNOTATION_RUNTIME_RETENTION));
		assertFalse(findTypeAnnotationFor(AnnotatedFields.class, TYPE_ANNOTATION_RUNTIME_RETENTION));
	}

	@Test
	public void findClassAnnotationInType() {
		assertTrue(findTypeAnnotationFor(AnnotatedWithClass.class, TYPE_ANNOTATION_CLASS_RETENTION));
		assertTrue(findTypeAnnotationFor(AnnotatedWithBoth.class, TYPE_ANNOTATION_CLASS_RETENTION));
		
		assertFalse(findTypeAnnotationFor(AnnotatedWithRuntime.class, TYPE_ANNOTATION_CLASS_RETENTION));
		assertFalse(findTypeAnnotationFor(AnnotatedFields.class, TYPE_ANNOTATION_CLASS_RETENTION));
	}

	@Test
	public void findRuntimeAnnotationInField() {
		assertTrue(findFieldAnnotationFor(RUNTIME_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION));
		assertTrue(findFieldAnnotationFor(BOTH_RETENTIONS, FIELD_ANNOTATION_RUNTIME_RETENTION));
	
		assertFalse(findFieldAnnotationFor(CLASS_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION));
		assertFalse(findFieldAnnotationFor(NO_RETENTION, FIELD_ANNOTATION_RUNTIME_RETENTION));
	}

	@Test
	public void findClassAnnotationInField() {
		assertTrue(findFieldAnnotationFor(CLASS_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION));
		assertTrue(findFieldAnnotationFor(BOTH_RETENTIONS, FIELD_ANNOTATION_CLASS_RETENTION));
	
		assertFalse(findFieldAnnotationFor(RUNTIME_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION));
		assertFalse(findFieldAnnotationFor(NO_RETENTION, FIELD_ANNOTATION_CLASS_RETENTION));
	}
	
	@Test
	public void findPartialAnnotationName() {
		assertTrue(findTypeAnnotationFor(AnnotatedWithRuntime.class, "TypeAnnotationRuntimeRetention"));
		assertTrue(findFieldAnnotationFor(RUNTIME_RETENTION, "FieldAnnotationRuntimeRetention"));
	}
	
	@Test
	public void findFullyQualifiedAnnotationName() {
		String canonicalTypeAnnotationName = TypeAnnotationRuntimeRetention.class.getCanonicalName();
		assertTrue(findTypeAnnotationFor(AnnotatedWithRuntime.class, canonicalTypeAnnotationName));
		
		String canonicalFieldAnnotationName = FieldAnnotationRuntimeRetention.class.getCanonicalName();
		assertTrue(findFieldAnnotationFor(RUNTIME_RETENTION, canonicalFieldAnnotationName));
	}
	
	private boolean findTypeAnnotationFor(Class<?> type, String annotationDescriptor) {
		AnnotationAccessor accessor = new AnnotationAccessor(type);
		return accessor.typeHas(annotationDescriptor);
	}
	
	private boolean findFieldAnnotationFor(String fieldName, String annotationDescriptor) {
		AnnotationAccessor accessor = new AnnotationAccessor(AnnotatedFields.class);
		return accessor.fieldHas(fieldName, annotationDescriptor);
	}

	@TypeAnnotationRuntimeRetention
	static class AnnotatedWithRuntime {}

	@TypeAnnotationClassRetention
	static class AnnotatedWithClass {}
	
	@TypeAnnotationRuntimeRetention
	@TypeAnnotationClassRetention
	static class AnnotatedWithBoth {}
	
	static class AnnotatedFields {
		@FieldAnnotationRuntimeRetention
		int runtimeRetention;
		
		@FieldAnnotationClassRetention
		int classRetention;
		
		@FieldAnnotationRuntimeRetention
		@FieldAnnotationClassRetention
		int bothRetentions;
		
		int noRetention;
	}
}
