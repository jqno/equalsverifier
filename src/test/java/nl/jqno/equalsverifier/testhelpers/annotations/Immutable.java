package nl.jqno.equalsverifier.testhelpers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Because there's no standard Immutable annotation, we define our own so that EqualsVerifier can
 * work with any annotation called Immutable.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Immutable {
}
