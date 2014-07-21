package nl.jqno.equalsverifier.integration.extended_contract;

import java.io.File;
import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.ConditionalCompiler;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Java8ApiClassesTest extends IntegrationTestBase {
	private File tempFileLocation;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		tempFileLocation = tempFolder.newFolder();
	}
	
	@Test
	public void successfullyInstantiatesAJava8ClassWithStreams_whenJava8IsAvailable() throws Exception {
		if (!isTypeAvailable("java.util.Optional")) {
			return;
		}
		
		ConditionalCompiler c = new ConditionalCompiler(tempFileLocation);
		try {
			Class<?> java8Class = c.compile(CLASS_NAME, CLASS);
			EqualsVerifier.forClass(java8Class)
					.verify();
		}
		finally {
			c.close();
		}
	}
	
	private static final String CLASS_NAME = "Java8ApiClassesContainer";
	private static final String CLASS =
			"\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;" +
			"\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
			"\nimport java.time.ZoneId;" +
			"\n" +
			"\npublic final class Java8ApiClassesContainer {" +
			"\n    private final ZoneId zoneId;" +
			"\n    " +
			"\n    public Java8ApiClassesContainer(ZoneId zoneId) {" +
			"\n        this.zoneId = zoneId;" +
			"\n    }" +
			"\n    " +
			"\n    @Override" +
			"\n    public boolean equals(Object obj) {" +
			"\n        return defaultEquals(this, obj);" +
			"\n    }" +
			"\n    " +
			"\n    @Override" +
			"\n    public int hashCode() {" +
			"\n        return defaultHashCode(this);" +
			"\n    }" +
			"\n}";
}
