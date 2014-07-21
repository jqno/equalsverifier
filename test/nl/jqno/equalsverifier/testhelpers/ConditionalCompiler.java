/*
 * Copyright 2014 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.testhelpers;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

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
			File sourceFile = writeSourceToFile(className, code);
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
	
	private File writeSourceToFile(String className, String code) throws IOException {
		FileWriter writer = null;
		try {
			File sourceFile = new File(tempFolder, className + ".java");
			writer = new FileWriter(sourceFile);
			writer.write(code);
			return sourceFile;
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	private void compileClass(File sourceFile) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = null;
		try {
			fileManager = compiler.getStandardFileManager(null, null, null);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempFolder));
			Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
			CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
			
			boolean success = task.call();
			if (!success) {
				throw new AssertionError("Could not compile the class");
			}
		}
		finally {
			if (fileManager != null) {
				fileManager.close();
			}
		}
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
		catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
		catch (InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}
}
