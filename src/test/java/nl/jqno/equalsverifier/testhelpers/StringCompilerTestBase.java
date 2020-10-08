package nl.jqno.equalsverifier.testhelpers;

import java.io.File;
import java.io.IOException;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class StringCompilerTestBase extends ExpectedExceptionTestBase {

    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

    private ConditionalCompiler compiler;

    @Before
    public void configureCompiler() throws IOException {
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

    public boolean isRecordsAvailable() {
        if (!isTypeAvailable("java.lang.Record")) {
            return false;
        }
        try {
            compileSimpleRecord();
            return true;
        } catch (AssertionError ignored) {
            // We're in Java 15 and preview features aren't enabled
            return false;
        }
    }

    public Class<?> compileSimpleRecord() {
        return compile(SIMPLE_RECORD_CLASS_NAME, SIMPLE_RECORD_CLASS);
    }

    private static final String SIMPLE_RECORD_CLASS_NAME = "SimpleRecord";
    private static final String SIMPLE_RECORD_CLASS = "record SimpleRecord(int i, String s) {}";
}
