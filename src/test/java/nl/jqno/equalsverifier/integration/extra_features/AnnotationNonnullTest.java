/*
 * Copyright 2014 Jan Ouwens
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
package nl.jqno.equalsverifier.integration.extra_features;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import javax.annotation.Nonnull;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;

import org.junit.Test;

public class AnnotationNonnullTest extends IntegrationTestBase {
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotation() {
		EqualsVerifier.forClass(NonnullByAnnotation.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationButOneDoesnt() {
		expectFailureWithCause(NullPointerException.class, "Non-nullity", "equals throws NullPointerException", "on field noAnnotation");
		EqualsVerifier.forClass(NonnullByAnnotationMissedOne.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationInSuperclass() {
		EqualsVerifier.forClass(SubclassNonnullByAnnotation.class)
				.verify();
	}
	
	static class NonnullByAnnotation {
		@Nonnull
		private final Object o;
		@NonNull
		private final Object p;
		@NotNull
		private final Object q;
		
		public NonnullByAnnotation(Object o, Object p, Object q) { this.o = o; this.p = p; this.q = q; }
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof NonnullByAnnotation)) {
				return false;
			}
			NonnullByAnnotation other = (NonnullByAnnotation)obj;
			return o.equals(other.o) && p.equals(other.p) && q.equals(other.q);
		}
		
		@Override public final int hashCode() { return defaultHashCode(this); }
	}
	
	static final class SubclassNonnullByAnnotation extends NonnullByAnnotation {
		public SubclassNonnullByAnnotation(Object o, Object p, Object q) { super(o, p, q); }
	}

	static final class NonnullByAnnotationMissedOne {
		@Nonnull
		private final Object o;
		// No annotation
		private final Object noAnnotation;
		@Nonnull
		private final Object q;
		
		public NonnullByAnnotationMissedOne(Object o, Object p, Object q) { this.o = o; this.noAnnotation = p; this.q = q; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullByAnnotationMissedOne)) {
				return false;
			}
			NonnullByAnnotationMissedOne other = (NonnullByAnnotationMissedOne)obj;
			return o.equals(other.o) && noAnnotation.equals(other.noAnnotation) && q.equals(other.q);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
