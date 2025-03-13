package nl.jqno.equalsverifier_testhelpers.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TypeAnnotationClassRetention
public @interface AnnotationWithAnnotation {}
