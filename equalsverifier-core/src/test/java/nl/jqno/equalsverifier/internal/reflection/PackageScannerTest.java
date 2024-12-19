package nl.jqno.equalsverifier.internal.reflection;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;

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

class PackageScannerTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(PackageScanner.class);
    }

    @Test
    void happyPath() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", null, false);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(A.class, B.class, C.class));
    }

    @Test
    void happyPathMustExtendClass() {
        List<Class<?>> classes = PackageScanner
                .getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperA.class, false);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubA1.class, SubA2.class));
    }

    @Test
    void happyPathMustExtendInterface() {
        List<Class<?>> classes = PackageScanner
                .getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperI.class, false);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubI1.class, SubI2.class));
    }

    @Test
    void happyPathRecursive() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.correct", null, true);
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
        List<Class<?>> classes = PackageScanner
                .getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.subclasses", SuperA.class, true);
        sort(classes);
        assertThat(classes).isEqualTo(Arrays.asList(SubA1.class, SubA2.class, SubA3.class));
    }

    @Test
    void filterOutTestClasses() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", null, false);
        List<Class<?>> testClasses =
                classes.stream().filter(c -> c.getName().endsWith("Test")).collect(Collectors.toList());
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void filterOutTestClassesRecursively() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.internal.reflection", null, true);
        List<Class<?>> testClasses =
                classes.stream().filter(c -> c.getName().endsWith("Test")).collect(Collectors.toList());
        assertThat(testClasses).isEqualTo(Collections.emptyList());
        assertThat(classes.size() - testClasses.size() > 0).isTrue();
    }

    @Test
    void nonexistentPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage", null, false);
        assertThat(classes).isEqualTo(Collections.emptyList());
    }

    @Test
    void nonexistentPackageAndSubPackage() {
        List<Class<?>> classes = PackageScanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage", null, true);
        assertThat(classes).isEqualTo(Collections.emptyList());
    }

    @Test
    void anonymousAndLocalClassesAreSkipped() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.testhelpers.packages.anonymous", null, false);

        assertThat(classes)
                .isEqualTo(Collections.singletonList(nl.jqno.equalsverifier.testhelpers.packages.anonymous.A.class));
    }

    private void sort(List<Class<?>> classes) {
        classes.sort(Comparator.comparing(Class::getName));
    }
}
