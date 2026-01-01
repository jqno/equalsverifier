package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.util.Validations;

/** Scans a package for classes. */
public final class PackageScanner {

    /**
     * Do not instantiate.
     */
    private PackageScanner() {}

    /**
     * Scans the given package for classes.
     *
     * Note that if {@code mustExtend} is given, and it exists within {@code packageName}, it will NOT be included.
     *
     * @param packageName The package to scan.
     * @param options     Modifications to the standard package scanning behaviour.
     * @return the classes contained in the given package.
     */
    public static List<Class<?>> getClassesIn(String packageName, PackageScanOptions options) {
        String packagePath = packageName.replace(".", FileSystems.getDefault().getSeparator());

        List<Class<?>> result = getResources(packagePath)
                .flatMap(r -> processResource(r, packagePath, options))
                .map(f -> fileToClass(f, packagePath))
                .filter(c -> !c.isAnonymousClass())
                .filter(c -> !c.isLocalClass())
                .filter(c -> !c.getName().endsWith("Test"))
                .filter(
                    c -> options.mustExtend() == null
                            || (options.mustExtend().isAssignableFrom(c) && !options.mustExtend().equals(c)))
                .distinct()
                .collect(Collectors.toList()); // Need a mutable List for the next validations

        Validations.validateTypesAreKnown(options.exceptClasses(), result);
        result.removeAll(options.exceptClasses());
        result.removeIf(options.exclusionPredicate());
        return result;
    }

    private static Stream<URL> getResources(String packagePath) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return rethrow(
            () -> Collections.list(cl.getResources(packagePath)).stream(),
            e -> "Could not resolve package " + packagePath + ": " + e.getMessage());
    }

    private static Stream<File> processResource(URL resource, String packagePath, PackageScanOptions options) {
        return rethrow(() -> switch (resource.toURI().getScheme()) {
            case "file" -> processDirectory(resource, options.scanRecursively());
            case "jar" -> options.ignoreExternalJars()
                    ? Stream.empty()
                    : walkJar(resource, packagePath, options.scanRecursively());
            default -> throw new ReflectionException(
                    "Could not resolve " + resource.toURI().getScheme() + " resource " + resource);
        }, e -> "Could not resolve resource " + resource + ": " + e.getMessage());
    }

    private static Stream<File> processDirectory(URL resource, boolean scanRecursively) throws URISyntaxException {
        String path = resource.toURI().getPath();
        return walkDirectory(new File(path), scanRecursively);
    }

    private static Stream<File> walkDirectory(File dir, boolean scanRecursively) {
        if (!dir.exists()) {
            return Stream.empty();
        }
        return Arrays
                .stream(dir.listFiles())
                .filter(f -> (scanRecursively && f.isDirectory()) || f.getName().endsWith(".class"))
                .flatMap(f -> f.isDirectory() ? walkDirectory(f, scanRecursively) : Stream.of(f));
    }

    private static Stream<File> walkJar(URL resource, String packagePath, boolean scanRecursively) throws IOException {
        String path = resource.getPath();
        String jar = path.substring(5, path.indexOf("!"));
        int packageSegments = packagePath.split("/").length;
        try (var file = new JarFile(jar)) {
            return file
                    .stream()
                    .map(e -> e.getName())
                    .filter(e -> e.endsWith(".class"))
                    .filter(e -> e.startsWith(packagePath))
                    .filter(e -> scanRecursively || e.split("/").length == packageSegments + 1)
                    .map(e -> new File(e))
                    .toList()
                    .stream();
        }
    }

    private static Class<?> fileToClass(File f, String packagePath) {
        String className = f.getName().substring(0, f.getName().length() - 6);
        String fullPath = f.getParent();
        String packageName =
                fullPath.substring(fullPath.indexOf(packagePath)).replace(FileSystems.getDefault().getSeparator(), ".");
        return rethrow(
            () -> Class.forName(packageName + "." + className),
            e -> "Could not resolve class " + className + ", which was found in package " + packageName);
    }
}
