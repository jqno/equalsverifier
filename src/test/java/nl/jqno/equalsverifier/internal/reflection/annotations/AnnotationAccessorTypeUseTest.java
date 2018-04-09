package nl.jqno.equalsverifier.internal.reflection.annotations;

import nl.jqno.equalsverifier.testhelpers.StringCompilerIntegrationTestBase;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;

public class AnnotationAccessorTypeUseTest extends StringCompilerIntegrationTestBase {
    private static final String JAVA_8_CLASS_NAME = "Java8Class";
    private static final String JAVA_8_CLASS =
            "\nimport org.eclipse.jdt.annotation.NonNull;" +
            "\n" +
            "\npublic final class Java8Class {" +
            "\n    private @NonNull String s;" +
            "\n}";

    @Test
    public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
        if (!isJava8Available()) {
            return;
        }

        Class<?> java8Class = compile(JAVA_8_CLASS_NAME, JAVA_8_CLASS);
        AnnotationAccessor accessor = new AnnotationAccessor(SupportedAnnotations.values(), java8Class, new HashSet<String>(), false);
        assertTrue(accessor.fieldHas("s", SupportedAnnotations.NONNULL));
    }

}
