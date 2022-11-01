package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import nl.jqno.equalsverifier.testhelpers.packages.correct.C;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SubA1;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SubA2;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SubI1;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SubI2;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperA;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.SuperI;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.subpackage.SubA3;
import org.junit.jupiter.api.Test;

public class PackageScannerTest {

    @Test
    public void coverTheConstructor() {
        coverThePrivateConstructor(PackageScanner.class);
    }

    @Test
    public void happyPath() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.correct",
            null,
            false
        );
        sort(classes);
        assertEquals(Arrays.asList(A.class, B.class, C.class), classes);
    }

    @Test
    public void happyPathMustExtendClass() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.subclasses",
            SuperA.class,
            false
        );
        sort(classes);
        assertEquals(Arrays.asList(SubA1.class, SubA2.class), classes);
    }

    @Test
    public void happyPathMustExtendInterface() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.subclasses",
            SuperI.class,
            false
        );
        sort(classes);
        assertEquals(Arrays.asList(SubI1.class, SubI2.class), classes);
    }

    @Test
    public void happyPathRecursive() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.correct",
            null,
            true
        );
        sort(classes);
        assertEquals(
            Arrays.asList(
                A.class,
                B.class,
                C.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.A.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.B.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.A.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.B.class,
                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.D.class
            ),
            classes
        );
    }

    @Test
    public void happyPathMustExtendClassRecursive() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.subclasses",
            SuperA.class,
            true
        );
        sort(classes);
        assertEquals(Arrays.asList(SubA1.class, SubA2.class, SubA3.class), classes);
    }

    @Test
    public void filterOutTestClasses() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.internal.reflection",
            null,
            false
        );
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
            "nl.jqno.equalsverifier.internal.reflection",
            null,
            true
        );
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
            "nl.jqno.equalsverifier.nonexistentpackage",
            null,
            false
        );
        assertEquals(Collections.emptyList(), classes);
    }

    @Test
    public void nonexistentPackageAndSubPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.nonexistentpackage",
            null,
            true
        );
        assertEquals(Collections.emptyList(), classes);
    }

    @Test
    public void anonymousAndLocalClassesAreSkipped() {
        List<Class<?>> classes = PackageScanner.getClassesIn(
            "nl.jqno.equalsverifier.testhelpers.packages.anonymous",
            null,
            false
        );

        assertEquals(
            Collections.singletonList(
                nl.jqno.equalsverifier.testhelpers.packages.anonymous.A.class
            ),
            classes
        );
    }

    private void sort(List<Class<?>> classes) {
        classes.sort(Comparator.comparing(Class::getName));
    }
}
