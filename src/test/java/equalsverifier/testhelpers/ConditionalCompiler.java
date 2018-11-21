package equalsverifier.testhelpers;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Compiles a class contained within a String.
 *
 * Useful for tests that refer to types that may or may not be present on the
 * classpath.
 */
public class ConditionalCompiler implements Closeable {
    private final File tempFolder;
    private final URLClassLoader classLoader;

    /**
     * Constructor.
     *
     * @param tempFolder
     *            To be determined in a unit test by:
     *            @Rule TemporaryFolder tempFolder = new TemporaryFolder();
     */
    public ConditionalCompiler(File tempFolder) {
        this.tempFolder = tempFolder;
        this.classLoader = createClassLoader(tempFolder);
    }

    private static URLClassLoader createClassLoader(File tempFolder) {
        try {
            URL[] urls = { tempFolder.toURI().toURL() };
            return new URLClassLoader(urls);
        }
        catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Compiles the class. The class must be in the default package.
     *
     * @param className
     *            Must match the name of the class given in {@code code}.
     * @param code
     *            The class to compile.
     * @return {@code code} as a compiled class.
     * @throws AssertionError
     *             If any part of the compilation fails.
     */
    public Class<?> compile(String className, String code) {
        try {
            JavaFileObject sourceFile = new StringJavaFileObject(className, code);
            compileClass(sourceFile);
            return classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new AssertionError("Failed to load newly compiled class:\n" + e.toString());
        }
        catch (IOException e) {
            throw new AssertionError("Failed to write file:\n" + e.toString());
        }
    }

    private void compileClass(JavaFileObject sourceFile) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = null;
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        try {
            fileManager = compiler.getStandardFileManager(collector, Locale.ROOT, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(tempFolder));
            CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, Collections.singletonList(sourceFile));

            boolean success = task.call();
            if (!success) {
                throw new AssertionError(buildErrorMessage(sourceFile, collector));
            }
        }
        finally {
            if (fileManager != null) {
                fileManager.close();
            }
        }
    }

    private String buildErrorMessage(JavaFileObject sourceFile, DiagnosticCollector<JavaFileObject> collector) {
        String result = "Could not compile class " + sourceFile.getName() + ":";
        List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();
        for (Diagnostic<? extends JavaFileObject> diag : diagnostics) {
            result += "\n" + diag.getKind() + " at " + diag.getLineNumber() + "," + diag.getColumnNumber() +
                    ": " + diag.getMessage(Locale.ROOT);
        }
        return result;
    }

    @Override
    public void close() {
        try {
            // URLClassLoader#close exists since Java 1.7,
            // so we'll have to call it reflectively in order to maintain Java 1.6 compatibility.
            Class<?> type = URLClassLoader.class;
            Method close = type.getDeclaredMethod("close");
            close.invoke(classLoader);
        }
        catch (NoSuchMethodException ignored) {
            // Java 6: do nothing; this code won't be reached anyway.
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    private static class StringJavaFileObject extends SimpleJavaFileObject {
        private final String code;

        protected StringJavaFileObject(String className, String code) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return code;
        }
    }
}
