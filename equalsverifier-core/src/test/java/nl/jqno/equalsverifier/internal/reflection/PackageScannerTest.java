package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.*;
import java.util.stream.Collectors;

import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import nl.jqno.equalsverifier.testhelpers.packages.correct.C;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.*;
import nl.jqno.equalsverifier.testhelpers.packages.subclasses.subpackage.SubA3;
import org.junit.jupiter.api.Test;

class PackageScannerTest {

    @Test
    void happyPath() {
        PackageScanner scanner = new PackageScanner();
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", null);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(A.class, B.class, C.class));
    }

    @Test
    void happyPathMustExtendClass() {
        PackageScanner scanner = new PackageScanner();
        List<Class<?>> classes =
                scanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperA.class);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubA1.class, SubA2.class));
    }

    @Test
    void happyPathMustExtendInterface() {
        PackageScanner scanner = new PackageScanner();
        List<Class<?>> classes =
                scanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperI.class);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubI1.class, SubI2.class));
    }

    @Test
    void happyPathRecursive() {
        PackageScanner scanner = new PackageScanner(ScanOption.recursive());
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", null);
        sort(classes);
        assertThat(classes)
                .isEqualTo(
                    Arrays
                            .asList(
                                A.class,
                                B.class,
                                C.class,
                                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.A.class,
                                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.B.class,
                                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.A.class,
                                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.B.class,
                                nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.D.class));
    }

    @Test
    void happyPathMustExtendClassRecursive() {
        PackageScanner scanner = new PackageScanner(ScanOption.recursive());
        List<Class<?>> classes =
                scanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperA.class);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubA1.class, SubA2.class, SubA3.class));
    }

    @Test
    void filterOutTestClasses() {
        PackageScanner scanner = new PackageScanner();
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", null);
        List<Class<?>> testClasses =
                classes.stream().filter(c -> c.getName().endsWith("Test")).collect(Collectors.toList());
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void filterOutTestClassesRecursively() {
        PackageScanner scanner = new PackageScanner(ScanOption.recursive());
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", null);
        List<Class<?>> testClasses =
                classes.stream().filter(c -> c.getName().endsWith("Test")).collect(Collectors.toList());
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void nonexistentPackage() {
        PackageScanner scanner = new PackageScanner();
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage", null);
        assertThat(classes).isEqualTo(Collections.emptyList());
    }

    @Test
    void nonexistentPackageAndSubPackage() {
        PackageScanner scanner = new PackageScanner(ScanOption.recursive());
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage", null);
        assertThat(classes).isEqualTo(Collections.emptyList());
    }

    @Test
    void anonymousAndLocalClassesAreSkipped() {
        PackageScanner scanner = new PackageScanner();
        List<Class<?>> classes = scanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.anonymous", null);

        assertThat(classes)
                .isEqualTo(Collections.singletonList(nl.jqno.equalsverifier.testhelpers.packages.anonymous.A.class));
    }

    @Test
    void dependencyPackage() {
        PackageScanner scanner = new PackageScanner();
        assertThatThrownBy(() -> scanner.getClassesIn("org.junit", null))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Could not resolve third-party resource");
    }

    private void sort(List<Class<?>> classes) {
        classes.sort(Comparator.comparing(Class::getName));
    }
}
