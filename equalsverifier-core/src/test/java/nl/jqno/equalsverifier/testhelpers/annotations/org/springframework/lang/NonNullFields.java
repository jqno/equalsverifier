package nl.jqno.equalsverifier.testhelpers.annotations.org.springframework.lang;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface NonNullFields {
}
