/*
 * Copyright 2015 Jan Ouwens
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
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.Java8IntegrationTestBase;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.Test;

public class AnnotationNonnullTypeUseTest extends Java8IntegrationTestBase {
	@Test
	public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
		if (!isJava8Available()) {
			return;
		}
		
		Class<?> type = compile(NONNULL_MANUAL_NAME, NONNULL_MANUAL);
		EqualsVerifier.forClass(type)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnClass() {
		EqualsVerifier.forClass(NonnullEclipseOnClass.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnOuterClass() {
		EqualsVerifier.forClass(NonnullEclipseOuter.FInner.class)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnNestedOuterClass() {
		EqualsVerifier.forClass(NonnullEclipseOuter.FMiddle.FInnerInner.class)
				.verify();
	}
	
	@Test
	public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
		if (!isJava8Available()) {
			return;
		}
		
		Class<?> type = compile(NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_NAME, NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS);
		expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
		EqualsVerifier.forClass(type)
				.verify();
	}
	
	@Test
	public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClassAndNullCheckInEquals() {
		if (!isJava8Available()) {
			return;
		}
		
		Class<?> type = compile(NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS_NAME, NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS);
		EqualsVerifier.forClass(type)
				.verify();
	}
	
	private static final String NONNULL_MANUAL_NAME = "NonnullManual";
	private static final String NONNULL_MANUAL =
			"\nimport java.util.Objects;" +
			"\nimport org.eclipse.jdt.annotation.NonNull;" +
			"\n" +
			"\nclass NonnullManual {" +
			"\n    private final @NonNull Object o;" +
			"\n" +
			"\n    public NonnullManual(Object o) { this.o = o; }" +
			"\n" +
			"\n    @Override" +
			"\n    public final boolean equals(Object obj) {" +
			"\n        if (!(obj instanceof NonnullManual)) {" +
			"\n            return false;" +
			"\n        }" +
			"\n        NonnullManual other = (NonnullManual)obj;" +
			"\n        return o.equals(other.o);" +
			"\n    }" +
			"\n" +
			"\n    @Override public final int hashCode() { return Objects.hash(o); }" +
			"\n}";
	
	@NonNullByDefault
	static final class NonnullEclipseOnClass {
		private final Object o;
		
		public NonnullEclipseOnClass(Object o) { this.o = o; }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NonnullEclipseOnClass)) {
				return false;
			}
			NonnullEclipseOnClass other = (NonnullEclipseOnClass)obj;
			return o.equals(other.o);
		}
		
		@Override public int hashCode() { return defaultHashCode(this); }
	}
	
	@NonNullByDefault
	static class NonnullEclipseOuter {
		static final class FInner {
			private final Object o;
			
			public FInner(Object o) { this.o = o; }
			
			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof FInner)) {
					return false;
				}
				FInner other = (FInner)obj;
				return o.equals(other.o);
			}
			
			@Override public int hashCode() { return defaultHashCode(this); }
		}
		
		static class FMiddle {
			static final class FInnerInner {
				private final Object o;
				
				public FInnerInner(Object o) { this.o = o; }
				
				@Override
				public boolean equals(Object obj) {
					if (!(obj instanceof FInnerInner)) {
						return false;
					}
					FInnerInner other = (FInnerInner)obj;
					return o.equals(other.o);
				}
				
				@Override public int hashCode() { return defaultHashCode(this); }
			}
		}
	}
	
	private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_NAME = "NonnullEclipseWithNullableOnClass";
	private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS =
			"\nimport java.util.Objects;" +
			"\nimport org.eclipse.jdt.annotation.NonNullByDefault;" +
			"\nimport org.eclipse.jdt.annotation.Nullable;" +
			"\n" +
			"\n@NonNullByDefault" +
			"\nclass NonnullEclipseWithNullableOnClass {" +
			"\n    private final @Nullable Object o;" +
			"\n" +
			"\n    public NonnullEclipseWithNullableOnClass(Object o) { this.o = o; }" +
			"\n" +
			"\n    @Override" +
			"\n    public final boolean equals(Object obj) {" +
			"\n        if (!(obj instanceof NonnullEclipseWithNullableOnClass)) {" +
			"\n            return false;" +
			"\n        }" +
			"\n        NonnullEclipseWithNullableOnClass other = (NonnullEclipseWithNullableOnClass)obj;" +
			"\n        return o.equals(other.o);" +
			"\n    }" +
			"\n" +
			"\n    @Override public final int hashCode() { return Objects.hash(o); }" +
			"\n}";
	
	private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS_NAME = "NonnullEclipseWithNullableOnClassAndNullCheckInEquals";
	private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS =
			"\nimport java.util.Objects;" +
			"\nimport org.eclipse.jdt.annotation.NonNullByDefault;" +
			"\nimport org.eclipse.jdt.annotation.Nullable;" +
			"\n" +
			"\n@NonNullByDefault" +
			"\nclass NonnullEclipseWithNullableOnClassAndNullCheckInEquals {" +
			"\n    private final @Nullable Object o;" +
			"\n" +
			"\n    public NonnullEclipseWithNullableOnClassAndNullCheckInEquals(Object o) { this.o = o; }" +
			"\n" +
			"\n    @Override" +
			"\n    public final boolean equals(Object obj) {" +
			"\n        if (!(obj instanceof NonnullEclipseWithNullableOnClassAndNullCheckInEquals)) {" +
			"\n            return false;" +
			"\n        }" +
			"\n        NonnullEclipseWithNullableOnClassAndNullCheckInEquals other = (NonnullEclipseWithNullableOnClassAndNullCheckInEquals)obj;" +
			"\n        return o == null ? other.o == null : o.equals(other.o);" +
			"\n    }" +
			"\n" +
			"\n    @Override public final int hashCode() { return Objects.hash(o); }" +
			"\n}";
}
