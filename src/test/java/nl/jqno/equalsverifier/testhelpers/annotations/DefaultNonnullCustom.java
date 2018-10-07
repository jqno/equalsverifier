package nl.jqno.equalsverifier.testhelpers.annotations;

import javax.annotation.meta.TypeQualifierDefault;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@NotNull
@TypeQualifierDefault(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface DefaultNonnullCustom {}
