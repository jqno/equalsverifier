package nl.jqno.equalsverifier.testhelpers.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Nonnull
@TypeQualifierDefault(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface DefaultNonnullInapplicable {}
