package nl.jqno.equalsverifier.testhelpers.annotations.org.jspecify.annotations;

import java.lang.annotation.*;

/**
 * This annotation serves as a placeholder for the real {@link org.jspecify.annotations.NullMarked} annotation. However,
 * since that annotation is compiled for Java 9, and this code base must support Java 8, we use this copy instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
public @interface NullMarked {}
