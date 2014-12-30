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
import nl.jqno.equalsverifier.integration.extra_features.jsr305_nonnull.custom_nonnull.Jsr305DefaultCustomNonnullOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.jsr305_nonnull.inapplicable.Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.jsr305_nonnull.multi.Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.jsr305_nonnull.single.Jsr305DefaultNonnullSingleTypeQualifierDefaultOnPackage;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultCustomNonnull;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullInapplicable;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullMulti;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullSingle;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;

import org.junit.Ignore;
import org.junit.Test;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;

@SuppressWarnings("deprecation")
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
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithFindbugsNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(Findbugs1xDefaultAnnotationNonnullOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithCustomNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(Findbugs1xDefaultAnnotationCustomNonnullOnClass.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationInSuperclass() {
		EqualsVerifier.forClass(SubclassNonnullByAnnotation.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithCustomNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(Jsr305DefaultCustomNonnullOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithSingleTypeQualifierDefaultOnClass() {
		EqualsVerifier.forClass(Jsr305DefaultNonnullSingleTypeQualifierDefaultOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithMultipleTypeQualifierDefaultOnClass() {
		EqualsVerifier.forClass(Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnClass.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithInapplicableTypeQualifierDefaultOnClass() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
		EqualsVerifier.forClass(Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithCustomNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(Jsr305DefaultCustomNonnullOnPackage.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithSingleTypeQualifierDefaultOnPackage() {
		EqualsVerifier.forClass(Jsr305DefaultNonnullSingleTypeQualifierDefaultOnPackage.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithMultipleTypeQualifierDefaultOnPackage() {
		EqualsVerifier.forClass(Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnPackage.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithInapplicableTypeQualifierDefaultOnPackage() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
		EqualsVerifier.forClass(Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnPackage.class)
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
	
	@DefaultAnnotation(Nonnull.class)
	static final class Findbugs1xDefaultAnnotationNonnullOnClass {
		private final Object o;
		
		public Findbugs1xDefaultAnnotationNonnullOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Findbugs1xDefaultAnnotationNonnullOnClass)) {
				return false;
			}
			Findbugs1xDefaultAnnotationNonnullOnClass other = (Findbugs1xDefaultAnnotationNonnullOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotation(NotNull.class)
	static final class Findbugs1xDefaultAnnotationCustomNonnullOnClass {
		private final Object o;
		
		public Findbugs1xDefaultAnnotationCustomNonnullOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Findbugs1xDefaultAnnotationCustomNonnullOnClass)) {
				return false;
			}
			Findbugs1xDefaultAnnotationCustomNonnullOnClass other = (Findbugs1xDefaultAnnotationCustomNonnullOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultCustomNonnull
	static final class Jsr305DefaultCustomNonnullOnClass {
		private final Object o;
		
		public Jsr305DefaultCustomNonnullOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Jsr305DefaultCustomNonnullOnClass)) {
				return false;
			}
			Jsr305DefaultCustomNonnullOnClass other = (Jsr305DefaultCustomNonnullOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}

	@DefaultNonnullSingle
	static final class Jsr305DefaultNonnullSingleTypeQualifierDefaultOnClass {
		private final Object o;
		
		public Jsr305DefaultNonnullSingleTypeQualifierDefaultOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Jsr305DefaultNonnullSingleTypeQualifierDefaultOnClass)) {
				return false;
			}
			Jsr305DefaultNonnullSingleTypeQualifierDefaultOnClass other = (Jsr305DefaultNonnullSingleTypeQualifierDefaultOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullMulti
	static final class Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnClass {
		private final Object o;
		
		public Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnClass)) {
				return false;
			}
			Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnClass other = (Jsr305DefaultNonnullMultipleTypeQualifierDefaultOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullInapplicable
	static final class Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnClass {
		private final Object o;
		
		public Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnClass)) {
				return false;
			}
			Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnClass other = (Jsr305DefaultNonnullInapplicableTypeQualifierDefaultOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
