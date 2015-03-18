package nl.jqno.equalsverifier.util.annotations;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import nl.jqno.equalsverifier.testhelpers.ConditionalCompiler;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AnnotationAccessorTypeAnnotationTest extends IntegrationTestBase {
	private static final String JAVA_8_CLASS_NAME = "Java8Class";
	private static final String JAVA_8_CLASS =
			"\nimport org.eclipse.jdt.annotation.NonNull;" +
			"\n" +
			"\npublic final class Java8Class {" +
			"\n    private @NonNull String s;" +
			"\n}";
	
	private ConditionalCompiler compiler;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		File tempFileLocation = tempFolder.newFolder();
		compiler = new ConditionalCompiler(tempFileLocation);
	}
	
	@After
	public void tearDown() {
		compiler.close();
	}
	
	@Test
	public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
		if (!isJava8Available()) {
			return;
		}
		
		Class<?> java8Class = compiler.compile(JAVA_8_CLASS_NAME, JAVA_8_CLASS);
		AnnotationAccessor accessor = new AnnotationAccessor(SupportedAnnotations.values(), java8Class, false);
		assertTrue(accessor.fieldHas("s", SupportedAnnotations.NONNULL));
	}

}
