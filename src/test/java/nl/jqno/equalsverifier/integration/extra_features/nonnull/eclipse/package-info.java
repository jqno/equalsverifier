/**
 * Applies Eclipse's NonNullByDefault annotation to the package.
 *
 * Actual classes in this package reside in AnnotationNonnullTypeUseTest,
 * because they're Java 8 only and need to be compiled conditionally.
 */
@NonNullByDefault
package nl.jqno.equalsverifier.integration.extra_features.nonnull.eclipse;

import nl.jqno.equalsverifier.testhelpers.annotations.org.eclipse.jdt.annotation.NonNullByDefault;
