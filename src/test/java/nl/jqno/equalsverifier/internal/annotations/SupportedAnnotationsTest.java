/*
 * Copyright 2016 Jan Ouwens
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
package nl.jqno.equalsverifier.internal.annotations;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SupportedAnnotationsTest {
    private static final Set<String> NO_IGNORED_ANNOTATIONS = new HashSet<>();

    @Test
    public void jsr305DefaultReturnsTrue_whenAnnotationHasNonnullAnnotation() {
        AnnotationProperties props = new AnnotationProperties("Lnl/jqno/equalsverifier/testhelpers/annotations/DefaultNonnullJavax;");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(props, NO_IGNORED_ANNOTATIONS);
        assertTrue(actual);
    }

    @Test
    public void jsr305DefaultReturnsFalse_whenAnnotationDoesntHaveNonnullAnnotation() {
        AnnotationProperties props = new AnnotationProperties("Ljavax/annotation/Nonnull;");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(props, NO_IGNORED_ANNOTATIONS);
        assertFalse(actual);
    }

    @Test
    public void jsr305DefaultReturnsFalse_whenTypeDoesNotExist() {
        AnnotationProperties props = new AnnotationProperties("Lnl/jqno/equalsverifier/TypeDoesNotExist;");
        boolean actual = SupportedAnnotations.JSR305_DEFAULT_ANNOTATION_NONNULL.validate(props, NO_IGNORED_ANNOTATIONS);
        assertFalse(actual);
    }
}
