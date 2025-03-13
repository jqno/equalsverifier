package nl.jqno.equalsverifier_testhelpers.annotations;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationWithValues {
    Class<? extends Annotation>[] annotations();

    ElementType elementType();
}
