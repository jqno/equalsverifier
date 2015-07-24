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
package nl.jqno.equalsverifier.util.annotations;

import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.testhelpers.Java8IntegrationTestBase;

import org.junit.Test;

public class AnnotationAccessorTypeUseTest extends Java8IntegrationTestBase {
    private static final String JAVA_8_CLASS_NAME = "Java8Class";
    private static final String JAVA_8_CLASS =
            "\nimport org.eclipse.jdt.annotation.NonNull;" +
            "\n" +
            "\npublic final class Java8Class {" +
            "\n    private @NonNull String s;" +
            "\n}";

    @Test
    public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
        if (!isJava8Available()) {
            return;
        }

        Class<?> java8Class = compile(JAVA_8_CLASS_NAME, JAVA_8_CLASS);
        AnnotationAccessor accessor = new AnnotationAccessor(SupportedAnnotations.values(), java8Class, false);
        assertTrue(accessor.fieldHas("s", SupportedAnnotations.NONNULL));
    }

}
