package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.util.Rethrow.rethrow;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/** Scans a package for classes. */
public final class PackageScanner {

    private final boolean scanRecursively;

    /**
     * Constructor.
     *
     * @param options Modifications to the standard package scanning behaviour.
     */
    public PackageScanner(ScanOption... options) {
        Set<ScanOption> opts = new HashSet<>();
        Collections.addAll(opts, options);
        this.scanRecursively = opts.contains(ScanOption.recursive());
    }

    /**
     * Scans the given package for classes.
     *
     * Note that if {@code mustExtend} is given, and it exists within {@code packageName}, it will NOT be included.
     *
     * @param packageName The package to scan.
     * @param mustExtend  if not null, returns only classes that extend or implement this class.
     * @return the classes contained in the given package.
     */
    public List<Class<?>> getClassesIn(String packageName, Class<?> mustExtend) {
        return getDirs(packageName)
                .stream()
                .flatMap(d -> getClassesInDir(packageName, d, mustExtend).stream())
                .collect(Collectors.toList());
    }

    private List<File> getDirs(String packageName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        return rethrow(
            () -> Collections
                    .list(cl.getResources(path))
                    .stream()
                    .map(r -> new File(getResourcePath(r)))
                    .collect(Collectors.toList()),
            e -> "Could not scan package " + packageName);
    }

    private String getResourcePath(URL r) {
        String result = rethrow(() -> r.toURI().getPath(), e -> "Could not resolve resource path: " + e.getMessage());
        if (result == null) {
            throw new ReflectionException("Could not resolve third-party resource " + r);
        }
        return result;
    }

    private List<Class<?>> getClassesInDir(String packageName, File dir, Class<?> mustExtend, ScanOption... options) {
        if (!dir.exists()) {
            return Collections.emptyList();
        }
        return Arrays
                .stream(dir.listFiles())
                .filter(f -> (scanRecursively && f.isDirectory()) || f.getName().endsWith(".class"))
                .flatMap(f -> {
                    List<Class<?>> classes;
                    if (f.isDirectory()) {
                        classes = getClassesInDir(packageName + "." + f.getName(), f, mustExtend, options);
                    }
                    else {
                        classes = Collections.singletonList(fileToClass(packageName, f));
                    }
                    return classes.stream();
                })
                .filter(c -> !c.isAnonymousClass())
                .filter(c -> !c.isLocalClass())
                .filter(c -> !c.getName().endsWith("Test"))
                .filter(c -> mustExtend == null || (mustExtend.isAssignableFrom(c) && !mustExtend.equals(c)))
                .collect(Collectors.toList());
    }

    private Class<?> fileToClass(String packageName, File file) {
        String className = file.getName().substring(0, file.getName().length() - 6);
        return rethrow(
            () -> Class.forName(packageName + "." + className),
            e -> "Could not resolve class " + className + ", which was found in package " + packageName);
    }
}
