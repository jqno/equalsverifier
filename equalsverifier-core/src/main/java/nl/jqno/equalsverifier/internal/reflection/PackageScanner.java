package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Scans a package for classes. */
public final class PackageScanner {

    /** Should not be instantiated. */
    private PackageScanner() {}

    /**
     * Scans the given package for classes.
     *
     * @param packageName The package to scan.
     * @param scanRecursively true to scan all sub-packages
     * @return the classes contained in the given package.
     */
    public static List<Class<?>> getClassesIn(String packageName, boolean scanRecursively) {
        return getDirs(packageName)
            .stream()
            .flatMap(d -> getClassesInDir(packageName, d, scanRecursively).stream())
            .collect(Collectors.toList());
    }

    private static List<File> getDirs(String packageName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        return rethrow(
            () ->
                Collections
                    .list(cl.getResources(path))
                    .stream()
                    .map(r -> new File(getResourcePath(r)))
                    .collect(Collectors.toList()),
            e -> "Could not scan package " + packageName
        );
    }

    private static String getResourcePath(URL r) {
        return rethrow(() -> r.toURI().getPath(), e -> "Could not resolve resource path: " + e.getMessage());
    }

    private static List<Class<?>> getClassesInDir(
        String packageName,
        File dir,
        boolean scanRecursively
    ) {
        if (!dir.exists()) {
            return Collections.emptyList();
        }
        return Arrays
            .stream(dir.listFiles())
            .filter(f -> (scanRecursively && f.isDirectory()) || f.getName().endsWith(".class"))
            .flatMap(f -> {
                List<Class<?>> classes;
                if (f.isDirectory()) {
                    classes = getClassesInDir(packageName + "." + f.getName(), f, scanRecursively);
                } else {
                    classes = Collections.singletonList(fileToClass(packageName, f));
                }
                return classes.stream();
            })
            .filter(c -> !c.isAnonymousClass())
            .filter(c -> !c.isLocalClass())
            .filter(c -> !c.getName().endsWith("Test"))
            .collect(Collectors.toList());
    }

    private static Class<?> fileToClass(String packageName, File file) {
        String className = file.getName().substring(0, file.getName().length() - 6);
        return rethrow(
            () -> Class.forName(packageName + "." + className),
            e ->
                "Could not resolve class " +
                className +
                ", which was found in package " +
                packageName
        );
    }
}
