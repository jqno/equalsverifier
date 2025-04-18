package nl.jqno.equalsverifier;

import java.lang.annotation.*;

/**
 * Allows tooling to discover if you forgot to call `#verify()`.
 *
 * @since 3.18
 */
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface CheckReturnValue {}
