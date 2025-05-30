package nl.jqno.equalsverifier_testhelpers.annotations.javax.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifier;

/**
 * The Java Platform Module System in Java 9 makes it hard access the javax.annotation.* annotations, because they live
 * in a dependency but end up inside a split package through the java.xml.ws.annotation module. The latter is slated to
 * be removed, but it hasn't happened yet in Java 9.
 *
 * <p>
 * Adding javax.annotation.Nonnull to a class works fine, but accessing it with reflection causes NoClassDefFoundErrors.
 *
 * <p>
 * This annotation can be used in tests to replace the regular javax.annotation.Nonnull.
 *
 * <p>
 * For more info, see https://blog.codefx.org/java/jsr-305-java-9/
 */
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {}
