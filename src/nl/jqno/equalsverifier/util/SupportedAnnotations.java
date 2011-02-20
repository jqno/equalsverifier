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

import java.util.Arrays;
import java.util.List;

import nl.jqno.equalsverifier.Annotation;

public enum SupportedAnnotations implements Annotation {
	IMMUTABLE("Immutable"),
	NONNULL("Nonnull", "NonNull", "NotNull");
	
	private final List<String> descriptors;

	private SupportedAnnotations(String... descriptors) {
		this.descriptors = Arrays.asList(descriptors);
	}
	
	public Iterable<String> descriptors() {
		return descriptors;
	}
}
