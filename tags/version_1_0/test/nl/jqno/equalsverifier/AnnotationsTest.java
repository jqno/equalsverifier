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
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeHashCode;
import nl.jqno.equalsverifier.testhelpers.Util;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;
import nl.jqno.equalsverifier.testhelpers.annotations.casefolding.Nonnull;
import nl.jqno.equalsverifier.testhelpers.points.ImmutableCanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.points.MutableCanEqualColorPoint;

import org.junit.Test;

public class AnnotationsTest {
	@Test
	public void immutable() {
		EqualsVerifier.forClass(ImmutableByAnnotation.class)
				.verify();
	}
	
	@Test
	public void immutableDoesntInherit() {
		EqualsVerifier.forClass(ImmutableCanEqualPoint.class)
				.withRedefinedSubclass(MutableCanEqualColorPoint.class)
				.verify();
		
		EqualsVerifier<MutableCanEqualColorPoint> ev = EqualsVerifier.forClass(MutableCanEqualColorPoint.class)
				.withRedefinedSuperclass();
		assertFailure(ev, "Mutability", "equals depends on mutable field", "color");
	}
	
	@Test
	public void nonnull() {
		EqualsVerifier.forClass(NonnullByAnnotation.class)
				.verify();
	}
	
	@Test
	public void nonnullMissedOne() {
		EqualsVerifier<NonnullByAnnotationMissedOne> ev = EqualsVerifier.forClass(NonnullByAnnotationMissedOne.class);
		assertFailure(ev, "Non-nullity", "equals throws NullPointerException", "on field noAnnotation");
	}
	
	@Test
	public void nonnullInherits() {
		EqualsVerifier.forClass(SubclassNonnullByAnnotation.class)
				.verify();
	}
	
	@Test
	public void entityHappyPath() {
		EqualsVerifier.forClass(EntityByJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void entityDoesntInherit() {
		EqualsVerifier<SubclassEntityByJpaAnnotation> ev = EqualsVerifier.forClass(SubclassEntityByJpaAnnotation.class);
		Util.assertFailure(ev, "Mutability");
	}
	
	@Test(expected=AssertionError.class)
	public void entityNonJpaAnnotation() {
		EqualsVerifier.forClass(EntityByNonJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void jpaTransient() {
		EqualsVerifier<TransientByJpaAnnotation> ev = EqualsVerifier.forClass(TransientByJpaAnnotation.class);
		assertFailure(ev, "Transient field", "should not be included in equals/hashCode contract");
	}
	
	@Test
	public void jpaTransientInherits() {
		EqualsVerifier<SubclassTransientByJpaAnnotation> ev = EqualsVerifier.forClass(SubclassTransientByJpaAnnotation.class);
		assertFailure(ev, "Transient field", "should not be included in equals/hashCode contract");
	}
	
	@Test
	public void ignoreNonJpaTransient() {
		EqualsVerifier.forClass(TransientByNonJpaAnnotation.class)
				.verify();
	}

	@Immutable
	public static final class ImmutableByAnnotation {
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
	
	static class NonnullByAnnotation {
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
		public final boolean equals(Object obj) {
			if (!(obj instanceof NonnullByAnnotation)) {
				return false;
			}
			NonnullByAnnotation other = (NonnullByAnnotation)obj;
			return o.equals(other.o) && p.equals(other.p) && q.equals(other.q);
		}
		
		@Override
		public final int hashCode() {
			int result = 0;
			result += 31 * o.hashCode();
			result += 31 * p.hashCode();
			result += 31 * q.hashCode();
			return result;
		}
	}
	
	static final class SubclassNonnullByAnnotation extends NonnullByAnnotation {
		SubclassNonnullByAnnotation(Object o, Object p, Object q) {
			super(o, p, q);
		}
	}

	static final class NonnullByAnnotationMissedOne {
		@Nonnull
		private final Object o;
		// No annotation
		private final Object noAnnotation;
		@Nonnull
		private final Object q;
		
		NonnullByAnnotationMissedOne(Object o, Object p, Object q) {
			this.o = o;
			this.noAnnotation = p;
			this.q = q;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullByAnnotationMissedOne)) {
				return false;
			}
			NonnullByAnnotationMissedOne other = (NonnullByAnnotationMissedOne)obj;
			return o.equals(other.o) && noAnnotation.equals(other.noAnnotation) && q.equals(other.q);
		}
		
		@Override
		public int hashCode() {
			int result = 0;
			result += 31 * o.hashCode();
			result += 31 * noAnnotation.hashCode();
			result += 31 * q.hashCode();
			return result;
		}
	}
	
	@javax.persistence.Entity
	static class EntityByJpaAnnotation {
		private int i;
		private String s;
		
		public void setI(int i) {
			this.i = i;
		}
		
		public void setS(String s) {
			this.s = s;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof EntityByJpaAnnotation)) {
				return false;
			}
			EntityByJpaAnnotation other = (EntityByJpaAnnotation)obj;
			return i == other.i && nullSafeEquals(s, other.s);
		}
		
		@Override
		public final int hashCode() {
			return i + (31 * nullSafeHashCode(s));
		}
	}
	
	static class SubclassEntityByJpaAnnotation extends EntityByJpaAnnotation {}
	
	@nl.jqno.equalsverifier.testhelpers.annotations.Entity
	static class EntityByNonJpaAnnotation {
		private int i;
		private String s;
		
		public void setI(int i) {
			this.i = i;
		}
		
		public void setS(String s) {
			this.s = s;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof EntityByNonJpaAnnotation)) {
				return false;
			}
			EntityByNonJpaAnnotation other = (EntityByNonJpaAnnotation)obj;
			return i == other.i && nullSafeEquals(s, other.s);
		}
		
		@Override
		public final int hashCode() {
			return i + (31 * nullSafeHashCode(s));
		}
	}
	
	static class TransientByJpaAnnotation {
		private final int i;
		
		@javax.persistence.Transient
		private final int j;
		
		public TransientByJpaAnnotation(int i, int j) {
			this.i = i;
			this.j = j;
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof TransientByJpaAnnotation)) {
				return false;
			}
			TransientByJpaAnnotation other = (TransientByJpaAnnotation)obj;
			return i == other.i && j == other.j;
		}
		
		@Override
		public final int hashCode() {
			return i + (31 * j);
		}
	}
	
	static final class SubclassTransientByJpaAnnotation extends TransientByJpaAnnotation {
		public SubclassTransientByJpaAnnotation(int i, int j) {
			super(i, j);
		}
	}
	
	static final class TransientByNonJpaAnnotation {
		private final int i;
		
		@nl.jqno.equalsverifier.testhelpers.annotations.Transient
		private final int j;
		
		public TransientByNonJpaAnnotation(int i, int j) {
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TransientByNonJpaAnnotation)) {
				return false;
			}
			TransientByNonJpaAnnotation other = (TransientByNonJpaAnnotation)obj;
			return i == other.i && j == other.j;
		}
		
		@Override
		public int hashCode() {
			return i + (31 * j);
		}
	}
}
