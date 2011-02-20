/*
 * Copyright 2011 Jan Ouwens
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
package nl.jqno.equalsverifier.testhelpers.annotations;

import java.util.Arrays;
import java.util.List;

import nl.jqno.equalsverifier.util.Annotation;

public enum TestSupportedAnnotations implements Annotation {
	TYPE_RUNTIME_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/TypeAnnotationRuntimeRetention;"),
	TYPE_CLASS_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/TypeAnnotationClassRetention;"),
	FIELD_RUNTIME_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/FieldAnnotationRuntimeRetention;"),
	FIELD_CLASS_RETENTION(false, "Lnl/jqno/equalsverifier/testhelpers/annotations/FieldAnnotationClassRetention;"),
	
	TYPE_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR(false, "TypeAnnotationRuntimeRetention"),
	TYPE_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR(false, TypeAnnotationRuntimeRetention.class.getCanonicalName()),
	
	FIELD_RUNTIME_RETENTION_PARTIAL_DESCRIPTOR(false, "FieldAnnotationRuntimeRetention"),
	FIELD_RUNTIME_RETENTION_CANONICAL_DESCRIPTOR(false, FieldAnnotationRuntimeRetention.class.getCanonicalName()),
	
	TYPE_INHERITS(true, "TypeAnnotationInherits"),
	TYPE_DOESNT_INHERIT(false, "TypeAnnotationDoesntInherit"),
	FIELD_INHERITS(true, "FieldAnnotationInherits"),
	FIELD_DOESNT_INHERIT(false, "FieldAnnotationDoesntInherit"),
	;
	
	private final boolean inherits;
	private final List<String> descriptors;

	private TestSupportedAnnotations(boolean inherits, String... descriptors) {
		this.inherits = inherits;
		this.descriptors = Arrays.asList(descriptors);
	}
	
	public Iterable<String> descriptors() {
		return descriptors;
	}
	
	@Override
	public boolean inherits() {
		return inherits;
	}
}
