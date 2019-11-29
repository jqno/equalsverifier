package nl.jqno.equalsverifier.testhelpers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifficultAnnotation {
    String[] strings() default {"a", "b", "c"};

    int[] ints() default {1, 2, 3};
}
