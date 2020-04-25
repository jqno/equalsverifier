package nl.jqno.equalsverifier.internal.reflection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;

/** Scans a package for classes. */
public final class PackageScanner {

    /** Should not be instantiated. */
    private PackageScanner() {}

    /**
     * Scans the given package for classes.
     *
     * @param packageName The package to scan.
     * @return the classes contained in the given package.
     */
    public static List<Class<?>> getClassesIn(String packageName) {
        return getDirs(packageName).stream()
                .flatMap(d -> getClassesInDir(packageName, d).stream())
                .collect(Collectors.toList());
    }

    private static List<File> getDirs(String packageName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        try {
            return Collections.list(cl.getResources(path)).stream()
                    .map(r -> new File(r.getFile()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ReflectionException("Could not scan package " + packageName, e);
        }
    }

    private static List<Class<?>> getClassesInDir(String packageName, File dir) {
        if (!dir.exists()) {
            return Collections.emptyList();
        }
        return Arrays.stream(dir.listFiles())
                .filter(f -> f.getName().endsWith(".class"))
                .map(f -> fileToClass(packageName, f))
                .collect(Collectors.toList());
    }

    private static Class<?> fileToClass(String packageName, File file) {
        String className = file.getName().substring(0, file.getName().length() - 6);
        try {
            return Class.forName(packageName + "." + className);
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(
                    "Could not resolve class "
                            + className
                            + ", which was found in package "
                            + packageName,
                    e);
        }
    }
}
