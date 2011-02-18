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
package nl.jqno.equalsverifier;

import static nl.jqno.equalsverifier.testhelpers.Util.assertFailure;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;
import nl.jqno.equalsverifier.testhelpers.annotations.casefolding.Nonnull;

import org.junit.Test;

public class AnnotationsTest {
	@Test
	public void immutable() {
		EqualsVerifier.forClass(ImmutableByAnnotation.class)
				.verify();
	}
	
	@Test
	public void nonnull() {
		EqualsVerifier.forClass(NonnullByAnnotation.class)
				.verify();
	}
	
	@Test
	public void nonnullMissedOne() {
		EqualsVerifier<NonnullByAnnotationMissedOne> ev = EqualsVerifier.forClass(NonnullByAnnotationMissedOne.class);
		assertFailure(ev, "Non-nullity", "equals throws NullPointerException");
	}

	@Immutable
	static final class ImmutableByAnnotation {
		private int i;
		
		ImmutableByAnnotation(int i) {
			this.i = i;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ImmutableByAnnotation)) {
				return false;
			}
			return i == ((ImmutableByAnnotation)obj).i;
		}
		
		@Override
		public int hashCode() {
			return i;
		}
	}
	
	static final class NonnullByAnnotation {
		@Nonnull
		private final Object o;
		@NonNull
		private final Object p;
		@NotNull
		private final Object q;
		
		NonnullByAnnotation(Object o, Object p, Object q) {
			this.o = o;
			this.p = p;
			this.q = q;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullByAnnotation)) {
				return false;
			}
			NonnullByAnnotation other = (NonnullByAnnotation)obj;
			return o.equals(other.o) && p.equals(other.p) && q.equals(other.q);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * o.hashCode();
			result += 31 * p.hashCode();
			result += 31 * q.hashCode();
			return result;
		}
	}
	
	static final class NonnullByAnnotationMissedOne {
		@Nonnull
		private final Object o;
		// No annotation
		private final Object p;
		@Nonnull
		private final Object q;
		
		NonnullByAnnotationMissedOne(Object o, Object p, Object q) {
			this.o = o;
			this.p = p;
			this.q = q;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullByAnnotationMissedOne)) {
				return false;
			}
			NonnullByAnnotationMissedOne other = (NonnullByAnnotationMissedOne)obj;
			return o.equals(other.o) && p.equals(other.p) && q.equals(other.q);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * o.hashCode();
			result += 31 * p.hashCode();
			result += 31 * q.hashCode();
			return result;
		}
	}
}
