package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.io.File;
import java.net.URL;
import java.util.*;
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
        List<Class<?>> result = getDirs(packageName, options)
                .stream()
                .flatMap(d -> getClassesInDir(packageName, d, options).stream())
                .collect(Collectors.toList());

        Validations.validateTypesAreKnown(options.exceptClasses, result);
        result.removeAll(options.exceptClasses);
        result.removeIf(options.exclusionPredicate);
        return result;
    }

    private static List<File> getDirs(String packageName, PackageScanOptions options) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        return rethrow(
            () -> Collections
                    .list(cl.getResources(path))
                    .stream()
                    .flatMap(r -> getResourcePath(r, options))
                    .collect(Collectors.toList()),
            e -> "Could not scan package " + packageName);
    }

    private static Stream<File> getResourcePath(URL r, PackageScanOptions options) {
        String result = rethrow(() -> r.toURI().getPath(), e -> "Could not resolve resource path: " + e.getMessage());
        if (result == null) {
            if (options.ignoreExternalJars) {
                return Stream.empty();
            }
            throw new ReflectionException("Could not resolve third-party resource " + r);
        }
        return Stream.of(new File(result));
    }

    private static List<Class<?>> getClassesInDir(String packageName, File dir, PackageScanOptions options) {
        if (!dir.exists()) {
            return Collections.emptyList();
        }
        return Arrays
                .stream(dir.listFiles())
                .filter(f -> (options.scanRecursively && f.isDirectory()) || f.getName().endsWith(".class"))
                .flatMap(f -> {
                    List<Class<?>> classes;
                    if (f.isDirectory()) {
                        classes = getClassesInDir(packageName + "." + f.getName(), f, options);
                    }
                    else {
                        classes = Collections.singletonList(fileToClass(packageName, f));
                    }
                    return classes.stream();
                })
                .filter(c -> !c.isAnonymousClass())
                .filter(c -> !c.isLocalClass())
                .filter(c -> !c.getName().endsWith("Test"))
                .filter(
                    c -> options.mustExtend == null
                            || (options.mustExtend.isAssignableFrom(c) && !options.mustExtend.equals(c)))
                .collect(Collectors.toList());
    }

    private static Class<?> fileToClass(String packageName, File file) {
        String className = file.getName().substring(0, file.getName().length() - 6);
        return rethrow(
            () -> Class.forName(packageName + "." + className),
            e -> "Could not resolve class " + className + ", which was found in package " + packageName);
    }
}
