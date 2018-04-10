package nl.jqno.equalsverifier.testhelpers.annotations.org.eclipse.jdt.annotation;

import java.lang.annotation.*;

/**
 * This annotation serves as a placeholder for the real
 * {@link org.eclipse.jdt.annotation.NonNullByDefault} annotation. However,
 * since that annotation is compiled for Java 8, and this code base must support
 * Java 6, we use this copy instead.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
public @interface NonNullByDefault {
}
