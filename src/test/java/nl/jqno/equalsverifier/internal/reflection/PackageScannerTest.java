package nl.jqno.equalsverifier.internal.reflection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.jqno.equalsverifier.testhelpers.packages.correct.*;
import org.junit.Test;

public class PackageScannerTest {

    private static final String SOME_PACKAGE =
            "nl.jqno.equalsverifier.testhelpers.packages.correct";

    @Test
    public void happyPath() {
        List<Class<?>> classes = PackageScanner.getClassesIn(SOME_PACKAGE);
        classes.sort((a, b) -> a.getName().compareTo(b.getName()));
        assertEquals(Arrays.asList(A.class, B.class, C.class), classes);
    }

    @Test
    public void nonexistentPackage() {
        List<Class<?>> classes =
                PackageScanner.getClassesIn("nl.jqno.equalsverifier.nonexistentpackage");
        assertEquals(Collections.emptyList(), classes);
    }
}
