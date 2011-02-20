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

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes an annotation that can be recognised by EqualsVerifier.
 * 
 * The annotation can have {@link RetentionPolicy#RUNTIME} or
 * {@link RetentionPolicy#CLASS}, and must have either {@link ElementType#TYPE}
 * or {@link ElementType#FIELD}.
 * 
 * @author Jan Ouwens
 */
public interface Annotation {
	/**
	 * One or more strings that contain the annotation's class name. A
	 * descriptor can be the annotation's fully qualified canonical name, or a
	 * substring thereof.
	 * 
	 * An annotation can be described by more than one descriptor. For
	 * instance, @Nonnull, @NonNull and @NotNull have the same semantics; their
	 * descriptors can be grouped together in one {@link Annotation} instance.
	 * 
	 * @return An Iterable of annotation descriptor strings.
	 */
	public Iterable<String> descriptors();
	
	/**
	 * Whether the annotation applies to the class in which is appears only, or
	 * whether it applies to that class and all its subclasses.
	 *
	 * @return True if the annotation is inherited by subclasses of the class
	 * 			in which the annotation appears.
	 */
	public boolean inherits();
}
