package nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToOne {
    FetchType fetch() default FetchType.EAGER;
}
