package nl.jqno.equalsverifier.verify_release.jar.helper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.function.Executable;

public class JarAsserter {

    private static final String EV = "nl/jqno/equalsverifier";

    private final JarReader reader;

    public JarAsserter(JarReader reader) {
        this.reader = reader;
    }

    public void assertPresenceOfMultiReleaseClasses() {
        assertPresenceOf(
            "/META-INF/versions/16/" + EV + "/internal/reflection/RecordsHelper.class",
            "/META-INF/versions/17/" + EV + "/internal/reflection/SealedClassesHelper.class"
        );
    }

    public void assertPresenceOf(String... fileNames) {
        var entries = reader.getEntries();
        assertForAll(
            entries::contains,
            fn ->
                "Expected presence of: " +
                fn +
                ", but it was absent.\nFilename: " +
                reader.getFilename(),
            fileNames
        );
    }

    public void assertAbsenceOf(String... fileNames) {
        var entries = reader.getEntries();
        assertForAll(
            fn -> !entries.contains(fn),
            fn ->
                "Expected absence of: " +
                fn +
                ", but it was present.\nFilename: " +
                reader.getFilename(),
            fileNames
        );
    }

    private void assertForAll(
        Predicate<String> assertion,
        Function<String, String> message,
        String... fileNames
    ) {
        Stream<Executable> assertions = Arrays
            .stream(fileNames)
            .map(fn -> () -> assertTrue(assertion.test(fn), message.apply(fn)));
        assertAll(assertions);
    }
}
