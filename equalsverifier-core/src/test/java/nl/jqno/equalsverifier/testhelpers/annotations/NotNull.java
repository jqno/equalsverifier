package nl.jqno.equalsverifier.testhelpers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Because there's no standard Nonnull/NonNull/NotNull annotation, we define our own so that
 * EqualsVerifier can work with any non-null annotation.
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface NotNull {
}
