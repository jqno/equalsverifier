package nl.jqno.equalsverifier;

import java.lang.annotation.*;

@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface CheckReturnValue {}
