package nl.jqno.equalsverifier.internal.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import nl.jqno.equalsverifier.ScanOption;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.packages.correct.B;
import nl.jqno.equalsverifier.testhelpers.packages.correct.C;
import nl.jqno.equalsverifier.testhelpers.packages.correct.subpackage.subpackage.D;
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
                                D.class));
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
        opts = PackageScanOptions
                .process(
                    ScanOption.except(c -> c.getSimpleName().endsWith("B")),
                    ScanOption.except(c -> c.getSimpleName().endsWith("C")));
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", opts);
        sort(classes);
        assertThat(classes).isEqualTo(List.of(A.class));
    }

    @Test
    void filterOutTestClasses() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", opts);
        List<Class<?>> testClasses = classes.stream().filter(c -> c.getName().endsWith("Test")).toList();
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void filterOutTestClassesRecursively() {
        opts = PackageScanOptions.process(ScanOption.recursive());
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", opts);
        List<Class<?>> testClasses = classes.stream().filter(c -> c.getName().endsWith("Test")).toList();
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
    void jarPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn("org.objenesis", opts);
        assertThat(classes)
                .anyMatch(c -> "org.objenesis.Objenesis".equals(c.getName()))
                .noneMatch(c -> "org.objenesis.instantiator.ObjectInstantiator".equals(c.getName()));
    }

    @Test
    void jarPackageRecursive() {
        opts = PackageScanOptions.process(ScanOption.recursive());
        List<Class<?>> classes = PackageScanner.getClassesIn("org.objenesis", opts);
        assertThat(classes)
                .anyMatch(c -> "org.objenesis.Objenesis".equals(c.getName()))
                .anyMatch(c -> "org.objenesis.instantiator.ObjectInstantiator".equals(c.getName()));
    }

    @Test
    void jarPackageWithIgnore() {
        opts = PackageScanOptions.process(ScanOption.ignoreExternalJars());
        List<Class<?>> classes = PackageScanner.getClassesIn("org.objenesis", opts);
        assertThat(classes).isEmpty();
    }

    @Test
    void jarPackageDontReturnEntireJar() {
        List<Class<?>> classes = PackageScanner.getClassesIn("org.objenesis.instantiator", opts);
        assertThat(classes)
                .noneMatch(c -> "org.objenesis.Objenesis".equals(c.getName()))
                .anyMatch(c -> "org.objenesis.instantiator.ObjectInstantiator".equals(c.getName()));
    }

    private void sort(List<Class<?>> classes) {
        classes.sort(Comparator.comparing(Class::getName));
    }
}
