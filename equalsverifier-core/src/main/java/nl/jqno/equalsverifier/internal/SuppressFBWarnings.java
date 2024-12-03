package nl.jqno.equalsverifier.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// CHECKSTYLE OFF: AbbreviationAsWordInName

/**
 * Copied over from spotbugs-annotations to avoid the dependency; see https://github.com/jqno/equalsverifier/issues/1026.
 */
@Retention(RetentionPolicy.CLASS)
public @interface SuppressFBWarnings {
    String[] value() default {};

    String justification() default "";
}
