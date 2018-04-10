/**
 * Applies a custom NonNull annotation to the whole package using Findbugs 1.x's DefaultAnnotation.
 */
@DefaultAnnotation(NonNull.class)
package nl.jqno.equalsverifier.integration.extra_features.nonnull.findbugs1x.custom;

import nl.jqno.equalsverifier.testhelpers.annotations.NonNull;
import nl.jqno.equalsverifier.testhelpers.annotations.edu.umd.cs.findbugs.annotations.DefaultAnnotation;
