/*
 * Copyright 2011, 2015 Jan Ouwens
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Descriptions of the annotations that {@link EqualsVerifier} supports.
 * 
 * The actual annotations cannot be referenced here, as that would create
 * dependencies on the libraries that contain them, and it would preclude
 * people from creating and using their own annotations with the same name.
 * 
 * @author Jan Ouwens
 */
public enum SupportedAnnotations implements Annotation {
	/**
	 * If a class is marked @Immutable, {@link EqualsVerifier} will not
	 * complain about fields not being final.
	 */
	IMMUTABLE(false, "Immutable"),
	
	/**
	 * If a field is marked @Nonnull (or @NonNull or @NotNull),
	 * {@link EqualsVerifier} will not complain about potential
	 * {@link NullPointerException}s being thrown if this field is null.
	 */
	NONNULL(true, "Nonnull", "NonNull", "NotNull"),
	
	/**
	 * JPA Entities cannot be final, nor can their fields be.
	 * {@link EqualsVerifier} will not complain about non-final fields
	 * on @Entity classes.
	 */
	ENTITY(false, "javax.persistence.Entity"),
	
	/**
	 * Fields in JPA Entities that are marked @Transient should not be included
	 * in the equals/hashCode contract, like fields that have the Java
	 * transient modifier. {@link EqualsVerifier} will treat these the same.
	 */
	TRANSIENT(true, "javax.persistence.Transient"),
	
	DEFAULT_ANNOTATION_NONNULL(false, "edu.umd.cs.findbugs.annotations.DefaultAnnotation") {
		@Override
		public boolean validateAnnotations(Map<String, Set<String>> annotations) {
			Set<String> values = annotations.get("value");
			if (values == null) {
				return false;
			}
			for (String value : values) {
				for (String descriptor : NONNULL.descriptors()) {
					if (value.contains(descriptor)) {
						return true;
					}
				}
			}
			return false;
		}
	},
	;
	
	private final boolean inherits;
	private final List<String> descriptors;

	private SupportedAnnotations(boolean inherits, String... descriptors) {
		this.inherits = inherits;
		this.descriptors = Arrays.asList(descriptors);
	}
	
	@Override
	public Iterable<String> descriptors() {
		return descriptors;
	}
	
	@Override
	public boolean inherits() {
		return inherits;
	}
	
	@Override
	public boolean validateAnnotations(Map<String, Set<String>> annotations) {
		return true;
	}
}
