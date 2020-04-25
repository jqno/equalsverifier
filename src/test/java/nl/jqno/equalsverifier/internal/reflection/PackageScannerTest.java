package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.testhelpers.packages.correct.*;
import org.junit.Test;

public class PackageScannerTest {

    @Test
    public void happyPath() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct");
        classes.sort((a, b) -> a.getName().compareTo(b.getName()));
        assertEquals(Arrays.asList(A.class, B.class, C.class), classes);
    }

    @Test
    public void filterOutTestClasses() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection");
        List<Class<?>> testClasses =
                classes.stream()
                        .filter(c -> c.getName().endsWith("Test"))
                        .collect(Collectors.toList());
        assertEquals(Collections.emptyList(), testClasses);
        assertTrue(classes.size() - testClasses.size() > 0);
    }

    @Test
    public void nonexistentPackage() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage");
        assertEquals(Collections.emptyList(), classes);
    }
}
