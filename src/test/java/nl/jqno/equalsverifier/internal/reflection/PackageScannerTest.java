package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.testhelpers.Util.coverThePrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.testhelpers.packages.correct.*;
import org.junit.jupiter.api.Test;

public class PackageScannerTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(PackageScanner.class);
    }

    @Test
    public void happyPath() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.correct", false);
        classes.sort((a, b) -> a.getName().compareTo(b.getName()));
        assertEquals(Arrays.asList(A.class, B.class, C.class), classes);
    }

    @Test
    public void happyPathRecursively() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
                "nl.jqno.equalsverifier.testhelpers.packages.correct", true);
        classes.sort(Comparator.comparing(Class::getName));
        assertEquals(Arrays.asList(
                A.class,
                B.class,
                C.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.A.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.B.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.A.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.B.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.D.class
        ), classes);
    }

    @Test
    public void filterOutTestClasses() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.internal.reflection", false);
        List<Class<?>> testClasses = classes
            .stream()
            .filter(c -> c.getName().endsWith("Test"))
            .collect(Collectors.toList());
        assertEquals(Collections.emptyList(), testClasses);
        assertTrue(classes.size() - testClasses.size() > 0);
    }

    @Test
    public void filterOutTestClassesRecursively() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
                "nl.jqno.equalsverifier.internal.reflection", true);
        List<Class<?>> testClasses = classes
                .stream()
                .filter(c -> c.getName().endsWith("Test"))
                .collect(Collectors.toList());
        assertEquals(Collections.emptyList(), testClasses);
        assertTrue(classes.size() - testClasses.size() > 0);
    }

    @Test
    public void nonexistentPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.nonexistentpackage", false);
        assertEquals(Collections.emptyList(), classes);
    }

    @Test
    public void nonexistentPackageAndSubPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
                "nl.jqno.equalsverifier.nonexistentpackage", true);
        assertEquals(Collections.emptyList(), classes);
    }
}
