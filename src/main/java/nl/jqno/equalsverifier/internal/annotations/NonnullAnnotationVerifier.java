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
package nl.jqno.equalsverifier.internal.annotations;

import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;

import java.lang.reflect.Field;

import static nl.jqno.equalsverifier.internal.annotations.SupportedAnnotations.*;

/**
 * Utility class that checks whether a field is marked with an Nonnull
 * annotation of some sort.
 */
public final class NonnullAnnotationVerifier {
    private NonnullAnnotationVerifier() {}

    /**
     * Checks whether the given field is marked with an Nonnull annotation,
     * whether directly, or through some default annotation mechanism.
     *
     * @param classAccessor An accessor for the class that contains the field.
     * @param field The field to be checked.
     * @return True if the field is to be treated as Nonnull.
     */
    public static boolean fieldIsNonnull(ClassAccessor<?> classAccessor, Field field) {
        if (classAccessor.fieldHasAnnotation(field, NONNULL)) {
            return true;
        }
        if (classAccessor.fieldHasAnnotation(field, NULLABLE)) {
            return false;
        }
        return annotationIsInScope(classAccessor, FINDBUGS1X_DEFAULT_ANNOTATION_NONNULL) ||
                annotationIsInScope(classAccessor, JSR305_DEFAULT_ANNOTATION_NONNULL) ||
                annotationIsInScope(classAccessor, ECLIPSE_DEFAULT_ANNOTATION_NONNULL);
    }

    private static boolean annotationIsInScope(ClassAccessor<?> classAccessor, Annotation annotation) {
        return classAccessor.hasAnnotation(annotation) ||
                classAccessor.outerClassHasAnnotation(annotation) ||
                classAccessor.packageHasAnnotation(annotation);
    }
}
