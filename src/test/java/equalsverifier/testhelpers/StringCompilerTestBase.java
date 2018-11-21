package equalsverifier.testhelpers;

import equalsverifier.reflection.ConditionalInstantiator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public abstract class StringCompilerTestBase extends ExpectedExceptionTestBase {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private ConditionalCompiler compiler;

    @Before
    public void setUp() throws IOException {
        File tempFileLocation = tempFolder.newFolder();
        compiler = new ConditionalCompiler(tempFileLocation);
    }

    @After
    public void tearDown() {
        compiler.close();
    }

    public Class<?> compile(String className, String code) {
        return compiler.compile(className, code);
    }

    public boolean isTypeAvailable(String fullyQualifiedTypeName) {
        return new ConditionalInstantiator(fullyQualifiedTypeName).resolve() != null;
    }
}
