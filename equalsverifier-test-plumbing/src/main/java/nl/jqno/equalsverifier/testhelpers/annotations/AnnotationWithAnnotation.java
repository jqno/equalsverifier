package nl.jqno.equalsverifier.testhelpers.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TypeAnnotationClassRetention
public @interface AnnotationWithAnnotation {
}
