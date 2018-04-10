package nl.jqno.equalsverifier.testhelpers.annotations.edu.umd.cs.findbugs.annotations;

import java.lang.annotation.*;

/**
 * This annotation serves as a paceholder for the real
 * {@link edu.umd.cs.findbugs.annotations.DefaultAnnotation}, which is
 * deprecated. We use this annotation to avoid warnings in places where we can't
 * suppress the warning.
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.CLASS)
public @interface DefaultAnnotation {
    Class<? extends Annotation>[] value();
}
