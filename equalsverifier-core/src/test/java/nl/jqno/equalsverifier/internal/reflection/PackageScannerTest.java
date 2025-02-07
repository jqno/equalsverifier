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

    private PackageScanOptions opts = new PackageScanOptions();

    @Test
    void happyPath() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", opts);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(A.class, B.class, C.class));
    }

    @Test
    void happyPathMustExtendClass() {
        opts = PackageScanOptions.process(ScanOption.mustExtend(SuperA.class));
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", opts);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubA1.class, SubA2.class));
    }

    @Test
    void happyPathMustExtendInterface() {
        opts = PackageScanOptions.process(ScanOption.mustExtend(SuperI.class));
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", opts);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubI1.class, SubI2.class));
    }

    @Test
    void happyPathRecursive() {
        opts = PackageScanOptions.process(ScanOption.recursive());
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", opts);
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
        opts = PackageScanOptions.process(ScanOption.recursive(), ScanOption.mustExtend(SuperA.class));
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", opts);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubA1.class, SubA2.class, SubA3.class));
    }

    @Test
    void happyPathExceptClasses() {
        opts = PackageScanOptions.process(ScanOption.except(B.class));
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", opts);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(A.class, C.class));
    }

    @Test
    void happyPathExceptPredicate() {
        opts = PackageScanOptions.process(ScanOption.except(c -> c.getSimpleName().endsWith("B")));
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", opts);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(A.class, C.class));
    }

    @Test
    void filterOutTestClasses() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", opts);
        List<Class<?>> testClasses =
                classes.stream().filter(c -> c.getName().endsWith("Test")).collect(Collectors.toList());
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void filterOutTestClassesRecursively() {
        opts = PackageScanOptions.process(ScanOption.recursive());
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", opts);
        List<Class<?>> testClasses =
                classes.stream().filter(c -> c.getName().endsWith("Test")).collect(Collectors.toList());
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void nonexistentPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage", opts);
        assertThat(classes).isEqualTo(Collections.emptyList());
    }

    @Test
    void nonexistentPackageAndSubPackage() {
        opts = PackageScanOptions.process(ScanOption.recursive());
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage", opts);
        assertThat(classes).isEqualTo(Collections.emptyList());
    }

    @Test
    void anonymousAndLocalClassesAreSkipped() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.anonymous", opts);

        assertThat(classes).isEqualTo(List.of(nl.jqno.equalsverifier.testhelpers.packages.anonymous.A.class));
    }

    @Test
    void dependencyPackage() {
        assertThatThrownBy(() -> PackageScanner.getClassesIn("org.junit", opts))
                .isInstanceOf(ReflectionException.class)
                .hasMessageContaining("Could not resolve third-party resource");
    }

    @Test
    void dependencyPackageWithIgnore() {
        opts = PackageScanOptions.process(ScanOption.ignoreExternalJars());
        List<Class<?>> classes = PackageScanner.getClassesIn("org.junit", opts);
        assertThat(classes).isEmpty();
    }

    private void sort(List<Class<?>> classes) {
        classes.sort(Comparator.comparing(Class::getName));
    }
}
