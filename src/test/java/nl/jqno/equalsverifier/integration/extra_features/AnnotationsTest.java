/*
 * Copyright 2011, 2013-2014 Jan Ouwens
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

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;
import static nl.jqno.equalsverifier.testhelpers.Util.nullSafeEquals;

import javax.annotation.Nonnull;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.Util;
import nl.jqno.equalsverifier.testhelpers.annotations.Immutable;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;
import nl.jqno.equalsverifier.testhelpers.types.FinalMethodsPoint;
import nl.jqno.equalsverifier.testhelpers.types.ImmutableCanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.types.MutableCanEqualColorPoint;
import nl.jqno.equalsverifier.util.Instantiator;

import org.junit.Test;

@SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
public class AnnotationsTest extends IntegrationTestBase {
	@Test
	public void succeed_whenClassHasNonfinalFields_givenImmutableAnnotation() {
		EqualsVerifier.forClass(ImmutableByAnnotation.class)
				.verify();
	}
	
	@Test
	public void succeed_whenRedefinableClassHasNonfinalFields_givenImmutableAnnotationAndAppropriateSubclass() {
		EqualsVerifier.forClass(ImmutableCanEqualPoint.class)
				.withRedefinedSubclass(MutableCanEqualColorPoint.class)
				.verify();
	}
	
	@Test
	public void fail_whenSuperclassHasImmutableAnnotationButThisClassDoesnt() {
		expectFailure("Mutability", "equals depends on mutable field", "color");
		EqualsVerifier.forClass(MutableCanEqualColorPoint.class)
				.withRedefinedSuperclass()
				.verify();
	}
	
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
	
	@Test
	public void succeed_whenFieldsAreMutable_givenClassHasJpaEntityAnnotation() {
		EqualsVerifier.forClass(EntityByJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void fail_whenFieldsAreMutable_givenSuperclassHasJpaEntityAnnotationButThisClassDoesnt() {
		expectFailure("Mutability");
		EqualsVerifier.forClass(SubclassEntityByJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void fail_whenClassIsJpaEntity_givenEntityAnnotationResidesInWrongPackage() {
		expectFailure("Mutability");
		EqualsVerifier.forClass(EntityByNonJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void fail_whenFieldsAreUsedInEquals_givenTheyHaveJpaTransientAnnotation() {
		expectFailure("Transient field", "should not be included in equals/hashCode contract");
		EqualsVerifier.forClass(TransientByJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void fail_whenFieldsAreUsedInEquals_givenTheyHaveJpaTransientAnnotationInSuperclass() {
		expectFailure("Transient field", "should not be included in equals/hashCode contract");
		EqualsVerifier.forClass(SubclassTransientByJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void succeed_whenFieldsAreUsedInEquals_givenTransientAnnotationResidesInWrongPackage() {
		EqualsVerifier.forClass(TransientByNonJpaAnnotation.class)
				.verify();
	}
	
	@Test
	public void fail_whenReadingAnnotationsFromDynamicClass() {
		FinalMethodsPoint dynamic = Instantiator.of(FinalMethodsPoint.class).instantiateAnonymousSubclass();
		expectFailure("Cannot read class file for", "Suppress Warning.ANNOTATION to skip annotation processing phase");
		EqualsVerifier.forClass(dynamic.getClass())
				.verify();
	}
	
	@Test
	public void succeed_whenClassIsDynamic_givenAnnotationWarningIsSuppressed() {
		FinalMethodsPoint dynamic = Instantiator.of(FinalMethodsPoint.class).instantiateAnonymousSubclass();
		EqualsVerifier.forClass(dynamic.getClass())
				.suppress(Warning.ANNOTATION)
				.verify();
	}
	
	@Test
	public void succeed_whenClassIsNotDynamic_givenAnnotationWarningIsSuppressed() {
		EqualsVerifier.forClass(ImmutableByAnnotation.class)
				.suppress(Warning.ANNOTATION)
				.verify();
	}

	@Immutable
	public static final class ImmutableByAnnotation {
		private int i;
		
		public ImmutableByAnnotation(int i) { this.i = i; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return Util.defaultHashCode(this); }
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
		
		@Override public final int hashCode() { return defaultHashCode(this); }
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
		
		@Override public final int hashCode() { return defaultHashCode(this); }
	}
	
	static class TransientByJpaAnnotation {
		private final int i;
		
		@javax.persistence.Transient
		private final int j;
		
		public TransientByJpaAnnotation(int i, int j) { this.i = i; this.j = j; }
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof TransientByJpaAnnotation)) {
				return false;
			}
			TransientByJpaAnnotation other = (TransientByJpaAnnotation)obj;
			return i == other.i && j == other.j;
		}
		
		@Override public final int hashCode() { return defaultHashCode(this); }
	}
	
	static final class SubclassTransientByJpaAnnotation extends TransientByJpaAnnotation {
		public SubclassTransientByJpaAnnotation(int i, int j) { super(i, j); }
	}
	
	static final class TransientByNonJpaAnnotation {
		private final int i;
		
		@nl.jqno.equalsverifier.testhelpers.annotations.Transient
		private final int j;
		
		public TransientByNonJpaAnnotation(int i, int j) { this.i = i; this.j = j; }
		
		@Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
