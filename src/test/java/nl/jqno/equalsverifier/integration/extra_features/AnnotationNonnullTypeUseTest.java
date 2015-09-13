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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.Java8IntegrationTestBase;
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
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_ON_CLASS_NAME, NONNULL_ECLIPSE_ON_CLASS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnPackage() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_ON_PACKAGE_NAME, NONNULL_ECLIPSE_ON_PACKAGE);
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnOuterClass() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> outer = compile(NONNULL_ECLIPSE_OUTER_NAME, NONNULL_ECLIPSE_OUTER);
        Class<?> fInner = findInnerClass(outer, "FInner");
        EqualsVerifier.forClass(fInner)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationOnNestedOuterClass() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> outer = compile(NONNULL_ECLIPSE_OUTER_NAME, NONNULL_ECLIPSE_OUTER);
        Class<?> fMiddle = findInnerClass(outer, "FMiddle");
        Class<?> fInnerInner = findInnerClass(fMiddle, "FInnerInner");
        EqualsVerifier.forClass(fInnerInner)
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
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnClassAndWarningSuppressed() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_NAME, NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS);
        EqualsVerifier.forClass(type)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenEqualsChecksForNull_givenEclipseDefaultAndNullableAnnotationOnClass() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS_NAME,
                NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnPackage() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_WITH_NULLABLE_ON_PACKAGE_NAME, NONNULL_ECLIPSE_WITH_NULLABLE_ON_PACKAGE);
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAndNullableAnnotationOnPackageAndWarningIsSuppressed() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_WITH_NULLABLE_ON_PACKAGE_NAME, NONNULL_ECLIPSE_WITH_NULLABLE_ON_PACKAGE);
        EqualsVerifier.forClass(type)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationButInapplicableLocationOnClass() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_WITH_INAPPLICABLE_LOCATION_ON_CLASS_NAME, NONNULL_ECLIPSE_WITH_INAPPLICABLE_LOCATION_ON_CLASS);
        expectFailure("Non-nullity", "equals throws NullPointerException", "on field o");
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsDoesntCheckForNull_givenEclipseDefaultAnnotationWithApplicableLocationOnClass() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(NONNULL_ECLIPSE_WITH_APPLICABLE_LOCATION_ON_CLASS_NAME, NONNULL_ECLIPSE_WITH_APPLICABLE_LOCATION_ON_CLASS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    private Class<?> findInnerClass(Class<?> outer, String name) {
        for (Class<?> inner : outer.getDeclaredClasses()) {
            if (inner.getName().endsWith(name)) {
                return inner;
            }
        }
        throw new IllegalStateException("Inner class not found");
    }

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
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

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String NONNULL_ECLIPSE_ON_CLASS_NAME = "NonnullEclipseOnClass";
    private static final String NONNULL_ECLIPSE_ON_CLASS =
            "\nimport java.util.Objects;" +
            "\nimport org.eclipse.jdt.annotation.NonNullByDefault;" +
            "\n" +
            "\n@NonNullByDefault" +
            "\nfinal class NonnullEclipseOnClass {" +
            "\n    private final Object o;" +
            "\n" +
            "\n    public NonnullEclipseOnClass(Object o) { this.o = o; }" +
            "\n" +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof NonnullEclipseOnClass)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        NonnullEclipseOnClass other = (NonnullEclipseOnClass)obj;" +
            "\n        return o.equals(other.o);" +
            "\n    }" +
            "\n" +
            "\n    @Override public int hashCode() { return Objects.hash(o); }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 3 lines.
    private static final String NONNULL_ECLIPSE_ON_PACKAGE_NAME =
            "nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse.NonnullEclipseOnPackage";
    private static final String NONNULL_ECLIPSE_ON_PACKAGE =
            "\npackage nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse;" +
            "\n" +
            "\nimport java.util.Objects;" +
            "\n" +
            "\nfinal class NonnullEclipseOnPackage {" +
            "\n    private final Object o;" +
            "\n    " +
            "\n    public NonnullEclipseOnPackage(Object o) { this.o = o; }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof NonnullEclipseOnPackage)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        NonnullEclipseOnPackage other = (NonnullEclipseOnPackage)obj;" +
            "\n        return o.equals(other.o);" +
            "\n    }" +
            "\n    " +
            "\n    @Override public int hashCode() { return Objects.hash(o); }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String NONNULL_ECLIPSE_OUTER_NAME = "NonnullEclipseOuter";
    private static final String NONNULL_ECLIPSE_OUTER =
            "\nimport java.util.Objects;" +
            "\nimport org.eclipse.jdt.annotation.NonNullByDefault;" +
            "\n" +
            "\n@NonNullByDefault" +
            "\nclass NonnullEclipseOuter {" +
            "\n    static final class FInner {" +
            "\n        private final Object o;" +
            "\n" +
            "\n        public FInner(Object o) { this.o = o; }" +
            "\n" +
            "\n        @Override" +
            "\n        public boolean equals(Object obj) {" +
            "\n            if (!(obj instanceof FInner)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n            FInner other = (FInner)obj;" +
            "\n            return o.equals(other.o);" +
            "\n        }" +
            "\n" +
            "\n        @Override public int hashCode() { return Objects.hash(o); }" +
            "\n    }" +
            "\n" +
            "\n    static class FMiddle {" +
            "\n        static final class FInnerInner {" +
            "\n            private final Object o;" +
            "\n" +
            "\n            public FInnerInner(Object o) { this.o = o; }" +
            "\n" +
            "\n            @Override" +
            "\n            public boolean equals(Object obj) {" +
            "\n                if (!(obj instanceof FInnerInner)) {" +
            "\n                    return false;" +
            "\n                }" +
            "\n                FInnerInner other = (FInnerInner)obj;" +
            "\n                return o.equals(other.o);" +
            "\n            }" +
            "\n" +
            "\n            @Override public int hashCode() { return Objects.hash(o); }" +
            "\n        }" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
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

    // CHECKSTYLE: ignore DeclarationOrder for 3 lines.
    private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_CLASS_AND_NULLCHECK_IN_EQUALS_NAME =
            "NonnullEclipseWithNullableOnClassAndNullCheckInEquals";
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

    // CHECKSTYLE: ignore DeclarationOrder for 3 lines.
    private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_PACKAGE_NAME =
            "nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse.NonnullEclipseWithNullableOnPackageAndNullCheckInEquals";
    private static final String NONNULL_ECLIPSE_WITH_NULLABLE_ON_PACKAGE =
            "\npackage nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse;" +
            "\n" +
            "\nimport java.util.Objects;" +
            "\nimport org.eclipse.jdt.annotation.Nullable;" +
            "\n" +
            "\nclass NonnullEclipseWithNullableOnPackageAndNullCheckInEquals {" +
            "\n    private final @Nullable Object o;" +
            "\n" +
            "\n    public NonnullEclipseWithNullableOnPackageAndNullCheckInEquals(Object o) { this.o = o; }" +
            "\n" +
            "\n    @Override" +
            "\n    public final boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof NonnullEclipseWithNullableOnPackageAndNullCheckInEquals)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        NonnullEclipseWithNullableOnPackageAndNullCheckInEquals other =" +
            "\n                (NonnullEclipseWithNullableOnPackageAndNullCheckInEquals)obj;" +
            "\n        return o.equals(other.o);" +
            "\n    }" +
            "\n" +
            "\n    @Override public final int hashCode() { return Objects.hash(o); }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String NONNULL_ECLIPSE_WITH_INAPPLICABLE_LOCATION_ON_CLASS_NAME = "NonnullEclipseWithInapplicableLocationOnClass";
    private static final String NONNULL_ECLIPSE_WITH_INAPPLICABLE_LOCATION_ON_CLASS =
            "\nimport java.util.Objects;" +
            "\nimport org.eclipse.jdt.annotation.DefaultLocation;" +
            "\nimport org.eclipse.jdt.annotation.NonNullByDefault;" +
            "\n" +
            "\n@NonNullByDefault({ DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE })" +
            "\nfinal class NonnullEclipseWithInapplicableLocationOnClass {" +
            "\n    private final Object o;" +
            "\n" +
            "\n    public NonnullEclipseWithInapplicableLocationOnClass(Object o) { this.o = o; }" +
            "\n" +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof NonnullEclipseWithInapplicableLocationOnClass)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        NonnullEclipseWithInapplicableLocationOnClass other = (NonnullEclipseWithInapplicableLocationOnClass)obj;" +
            "\n        return o.equals(other.o);" +
            "\n    }" +
            "\n" +
            "\n    @Override public int hashCode() { return Objects.hash(o); }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String NONNULL_ECLIPSE_WITH_APPLICABLE_LOCATION_ON_CLASS_NAME = "NonnullEclipseWithApplicableLocationOnClass";
    private static final String NONNULL_ECLIPSE_WITH_APPLICABLE_LOCATION_ON_CLASS =
            "\nimport java.util.Objects;" +
            "\nimport org.eclipse.jdt.annotation.DefaultLocation;" +
            "\nimport org.eclipse.jdt.annotation.NonNullByDefault;" +
            "\n" +
            "\n@NonNullByDefault({ DefaultLocation.FIELD, DefaultLocation.RETURN_TYPE })" +
            "\nfinal class NonnullEclipseWithApplicableLocationOnClass {" +
            "\n    private final Object o;" +
            "\n" +
            "\n    public NonnullEclipseWithApplicableLocationOnClass(Object o) { this.o = o; }" +
            "\n" +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof NonnullEclipseWithApplicableLocationOnClass)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        NonnullEclipseWithApplicableLocationOnClass other = (NonnullEclipseWithApplicableLocationOnClass)obj;" +
            "\n        return o.equals(other.o);" +
            "\n    }" +
            "\n" +
            "\n    @Override public int hashCode() { return Objects.hash(o); }" +
            "\n}";
}
