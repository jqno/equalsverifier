package nl.jqno.equalsverifier.testhelpers.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;
import nl.jqno.equalsverifier.testhelpers.annotations.javax.annotation.Nonnull;

@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface DefaultNonnullJavax {
}
