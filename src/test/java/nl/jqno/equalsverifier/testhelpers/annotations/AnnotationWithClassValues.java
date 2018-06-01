package nl.jqno.equalsverifier.testhelpers.annotations;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationWithClassValues {
    Class<? extends Annotation>[] annotations();
}
