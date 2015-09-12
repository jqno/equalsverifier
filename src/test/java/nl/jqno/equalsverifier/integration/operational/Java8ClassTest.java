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
package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.Java8IntegrationTestBase;
import org.junit.Test;

/**
 * Tests that EqualsVerifier can handle a Java 8 class with streams.
 *
 * Since we want to maintain compatibility with Java 6 and 7, we need to
 * detect whether a Java 8 runtime is present and if so, compile and load
 * the class at run-time and then pass it to EqualsVerifier.
 */
public class Java8ClassTest extends Java8IntegrationTestBase {
    private static final String JAVA_8_CLASS_NAME = "Java8Class";
    private static final String JAVA_8_CLASS =
            "\nimport java.util.List;" +
            "\nimport java.util.Objects;" +
            "\n" +
            "\npublic final class Java8Class {" +
            "\n    private final List<Object> objects;" +
            "\n    " +
            "\n    public Java8Class(List<Object> objects) {" +
            "\n        this.objects = objects;" +
            "\n    }" +
            "\n    " +
            "\n    public void doSomethingWithStreams() {" +
            "\n        objects.stream().forEach(System.out::println);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof Java8Class)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        return Objects.equals(objects, ((Java8Class)obj).objects);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return Objects.hash(objects);" +
            "\n    }" +
            "\n}";

    private static final String JAVA_8_CLASS_WITH_SYNTHETIC_FIELD_NAME = "Java8ClassWithSyntheticField";
    private static final String JAVA_8_CLASS_WITH_SYNTHETIC_FIELD =
            "\nimport java.util.Comparator;" +
            "\nimport java.util.Objects;" +
            "\n" +
            "\npublic final class Java8ClassWithSyntheticField {" +
            "\n    private static final Comparator<Java8ClassWithSyntheticField> COMPARATOR =" +
            "\n            (c1, c2) -> 0;   // A lambda is a synthetic class" +
            "\n" +
            "\n    private final String s;" +
            "\n    " +
            "\n    public Java8ClassWithSyntheticField(String s) {" +
            "\n        this.s = s;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof Java8ClassWithSyntheticField)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        return Objects.equals(s, ((Java8ClassWithSyntheticField)obj).s);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return Objects.hash(s);" +
            "\n    }" +
            "\n}";

    @Test
    public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
        if (!isJava8Available()) {
            return;
        }

        Class<?> java8Class = compile(JAVA_8_CLASS_NAME, JAVA_8_CLASS);
        EqualsVerifier.forClass(java8Class)
                .verify();
    }

    @Test
    public void equalsverifierSucceeds_whenOneOfTheFieldsIsSynthetic() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> java8ClassWithSyntheticField = compile(JAVA_8_CLASS_WITH_SYNTHETIC_FIELD_NAME, JAVA_8_CLASS_WITH_SYNTHETIC_FIELD);
        EqualsVerifier.forClass(java8ClassWithSyntheticField)
                .verify();
    }
}
