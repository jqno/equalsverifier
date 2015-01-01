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
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom.NonnullFindbugs1xCustomOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax.NonnullFindbugs1xJavaxOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.custom.NonnullJsr305CustomOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.inapplicable.NonnullJsr305InapplicableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.javax.NonnullJsr305JavaxOnPackage;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullCustom;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullInapplicable;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullJavax;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;

import org.junit.Ignore;
import org.junit.Test;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;

@SuppressWarnings("deprecation")
public class AnnotationNonnullTest extends IntegrationTestBase {
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotation() {
		EqualsVerifier.forClass(NonnullManual.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationButOneDoesnt() {
		expectFailureWithCause(NullPointerException.class, "Non-nullity", "equals throws NullPointerException", "on field noAnnotation");
		EqualsVerifier.forClass(NonnullManualMissedOne.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFieldsHaveNonnullAnnotationInSuperclass() {
		EqualsVerifier.forClass(SubclassNonnullManual.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithJavaxNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(NonnullFindbugs1xJavaxOnClass.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithCustomNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(NonnullFindbugs1xCustomOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithJavaxNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullFindbugs1xJavaxOnPackage.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithCustomNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullFindbugs1xCustomOnPackage.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithJavaxNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(NonnullJsr305JavaxOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithCustomNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(NonnullJsr305CustomOnClass.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithInapplicableTypeQualifierDefaultOnClass() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
		EqualsVerifier.forClass(NonnullJsr305InapplicableOnClass.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithJavaxNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullJsr305JavaxOnPackage.class)
				.verify();
	}
	
	@Test@Ignore("Pending Issue 50")
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithCustomNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullJsr305CustomOnPackage.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithInapplicableTypeQualifierDefaultOnPackage() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
		EqualsVerifier.forClass(NonnullJsr305InapplicableOnPackage.class)
				.verify();
	}

	static class NonnullManual {
		@Nonnull
		private final Object o;
		@NonNull
		private final Object p;
		@NotNull
		private final Object q;
		
		public NonnullManual(Object o, Object p, Object q) { this.o = o; this.p = p; this.q = q; }
		
		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof NonnullManual)) {
				return false;
			}
			NonnullManual other = (NonnullManual)obj;
			return o.equals(other.o) && p.equals(other.p) && q.equals(other.q);
		}
		
		@Override public final int hashCode() { return defaultHashCode(this); }
	}
	
	static final class SubclassNonnullManual extends NonnullManual {
		public SubclassNonnullManual(Object o, Object p, Object q) { super(o, p, q); }
	}

	static final class NonnullManualMissedOne {
		@Nonnull
		private final Object o;
		// No annotation
		private final Object noAnnotation;
		@Nonnull
		private final Object q;
		
		public NonnullManualMissedOne(Object o, Object p, Object q) { this.o = o; this.noAnnotation = p; this.q = q; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullManualMissedOne)) {
				return false;
			}
			NonnullManualMissedOne other = (NonnullManualMissedOne)obj;
			return o.equals(other.o) && noAnnotation.equals(other.noAnnotation) && q.equals(other.q);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotation(Nonnull.class)
	static final class NonnullFindbugs1xJavaxOnClass {
		private final Object o;
		
		public NonnullFindbugs1xJavaxOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xJavaxOnClass)) {
				return false;
			}
			NonnullFindbugs1xJavaxOnClass other = (NonnullFindbugs1xJavaxOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotation(NotNull.class)
	static final class NonnullFindbugs1xCustomOnClass {
		private final Object o;
		
		public NonnullFindbugs1xCustomOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xCustomOnClass)) {
				return false;
			}
			NonnullFindbugs1xCustomOnClass other = (NonnullFindbugs1xCustomOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullJavax
	static final class NonnullJsr305JavaxOnClass {
		private final Object o;
		
		public NonnullJsr305JavaxOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullJsr305JavaxOnClass)) {
				return false;
			}
			NonnullJsr305JavaxOnClass other = (NonnullJsr305JavaxOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullCustom
	static final class NonnullJsr305CustomOnClass {
		private final Object o;
		
		public NonnullJsr305CustomOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullJsr305CustomOnClass)) {
				return false;
			}
			NonnullJsr305CustomOnClass other = (NonnullJsr305CustomOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullInapplicable
	static final class NonnullJsr305InapplicableOnClass {
		private final Object o;
		
		public NonnullJsr305InapplicableOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullJsr305InapplicableOnClass)) {
				return false;
			}
			NonnullJsr305InapplicableOnClass other = (NonnullJsr305InapplicableOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
}
