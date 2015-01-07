/*
 * Copyright 2014-2015 Jan Ouwens
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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom.NonnullFindbugs1xCustomOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom.NonnullFindbugs1xWithCheckForNullOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax.NonnullFindbugs1xJavaxOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.javax.NonnullFindbugs1xWithNullableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.custom.NonnullJsr305CustomOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.custom.NonnullJsr305WithNullableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.inapplicable.NonnullJsr305InapplicableOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.javax.NonnullJsr305JavaxOnPackage;
import nl.jqno.equalsverifier.integration.extra_features.nonnull.jsr305.javax.NonnullJsr305WithCheckForNullOnPackage;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullCustom;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullInapplicable;
import nl.jqno.equalsverifier.testhelpers.annotations.DefaultNonnullJavax;
import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.NotNull;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.DefaultAnnotationForFields;
import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;

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
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithJavaxNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullFindbugs1xJavaxOnPackage.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationWithCustomNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullFindbugs1xCustomOnPackage.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationForFields() {
		EqualsVerifier.forClass(NonnullFindbugs1xForFields.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAnnotationForParameters() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
		EqualsVerifier.forClass(NonnullFindbugs1xForParameters.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndNullableAnnotationOnClass() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullFindbugs1xWithNullableOnClass.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndCheckForNullAnnotationOnClass() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullFindbugs1xWithCheckForNullOnClass.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndNullableAnnotationOnPackage() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullFindbugs1xWithNullableOnPackage.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenFindbugs1xDefaultAndCheckForNullAnnotationOnPackage() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullFindbugs1xWithCheckForNullOnPackage.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoenstCheckForNull_givenJsr305DefaultAndCheckForNullOnPackageAndWarningSuppressed() {
		EqualsVerifier.forClass(NonnullFindbugs1xWithCheckForNullOnPackage.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndNullableAnnotationOnClassAndNullCheckInEquals() {
		EqualsVerifier.forClass(NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithJavaxNonnullAnnotationOnClass() {
		EqualsVerifier.forClass(NonnullJsr305JavaxOnClass.class)
				.verify();
	}
	
	@Test
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
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAnnotationWithJavaxNonnullAnnotationOnPackage() {
		EqualsVerifier.forClass(NonnullJsr305JavaxOnPackage.class)
				.verify();
	}
	
	@Test
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
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndNullableAnnotationOnClass() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullJsr305WithNullableOnClass.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndCheckForNullAnnotationOnClass() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullJsr305WithCheckForNullOnClass.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndNullableAnnotationOnPackage() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullJsr305WithNullableOnPackage.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndCheckForNullAnnotationOnPackage() {
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field p");
		EqualsVerifier.forClass(NonnullJsr305WithCheckForNullOnPackage.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoenstCheckForNull_givenJsr305DefaultAndNullableOnPackageAndWarningSuppressed() {
		EqualsVerifier.forClass(NonnullJsr305WithNullableOnPackage.class)
				.suppress(Warning.NULL_FIELDS)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenJsr305DefaultAndCheckForNullAnnotationOnClassAndNullCheckInEquals() {
		EqualsVerifier.forClass(NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals.class)
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
	
	@DefaultAnnotationForFields(NotNull.class)
	static final class NonnullFindbugs1xForFields {
		private final Object o;
		
		public NonnullFindbugs1xForFields(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xForFields)) {
				return false;
			}
			NonnullFindbugs1xForFields other = (NonnullFindbugs1xForFields)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotationForParameters(NotNull.class)
	static final class NonnullFindbugs1xForParameters {
		private final Object o;
		
		public NonnullFindbugs1xForParameters(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xForParameters)) {
				return false;
			}
			NonnullFindbugs1xForParameters other = (NonnullFindbugs1xForParameters)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotation(Nonnull.class)
	static final class NonnullFindbugs1xWithNullableOnClass {
		private final Object o;
		@Nullable
		private final Object p;
		
		public NonnullFindbugs1xWithNullableOnClass(Object o, Object p) { this.o = o; this.p = p; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xWithNullableOnClass)) {
				return false;
			}
			NonnullFindbugs1xWithNullableOnClass other = (NonnullFindbugs1xWithNullableOnClass)obj;
			return o.equals(other.o) && p.equals(other.p);
		}
		
		@Override
		public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotation(Nonnull.class)
	static final class NonnullFindbugs1xWithCheckForNullOnClass {
		private final Object o;
		@CheckForNull
		private final Object p;
		
		public NonnullFindbugs1xWithCheckForNullOnClass(Object o, Object p) { this.o = o; this.p = p; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xWithCheckForNullOnClass)) {
				return false;
			}
			NonnullFindbugs1xWithCheckForNullOnClass other = (NonnullFindbugs1xWithCheckForNullOnClass)obj;
			return o.equals(other.o) && p.equals(other.p);
		}
		
		@Override
		public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultAnnotation(Nonnull.class)
	static final class NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals {
		private final Object o;
		@Nullable
		private final Object p;
		
		public NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals(Object o, Object p) { this.o = o; this.p = p; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals)) {
				return false;
			}
			NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals other = (NonnullFindbugs1xWithNullableOnClassAndNullCheckInEquals)obj;
			return o.equals(other.o) && 
					(p == null ? other.p == null : p.equals(other.p));
		}
		
		@Override
		public int hashCode() { return defaultHashCode(this); }
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
	
	@DefaultNonnullJavax
	static final class NonnullJsr305WithNullableOnClass {
		private final Object o;
		@Nullable
		private final Object p;
		
		public NonnullJsr305WithNullableOnClass(Object o, Object p) { this.o = o; this.p = p; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullJsr305WithNullableOnClass)) {
				return false;
			}
			NonnullJsr305WithNullableOnClass other = (NonnullJsr305WithNullableOnClass)obj;
			return o.equals(other.o) && p.equals(other.p);
		}
		
		@Override
		public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullJavax
	static final class NonnullJsr305WithCheckForNullOnClass {
		private final Object o;
		@CheckForNull
		private final Object p;
		
		public NonnullJsr305WithCheckForNullOnClass(Object o, Object p) { this.o = o; this.p = p; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullJsr305WithCheckForNullOnClass)) {
				return false;
			}
			NonnullJsr305WithCheckForNullOnClass other = (NonnullJsr305WithCheckForNullOnClass)obj;
			return o.equals(other.o) && p.equals(other.p);
		}
		
		@Override
		public int hashCode() { return defaultHashCode(this); }
	}
	
	@DefaultNonnullJavax
	static final class NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals {
		private final Object o;
		@CheckForNull
		private final Object p;
		
		public NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals(Object o, Object p) { this.o = o; this.p = p; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals)) {
				return false;
			}
			NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals other = (NonnullJsr305WithCheckForNullOnClassAndNullCheckInEquals)obj;
			return o.equals(other.o) &&
					(p == null ? other.p == null : p.equals(other.p));
		}
		
		@Override
		public int hashCode() { return defaultHashCode(this); }
	}
}
