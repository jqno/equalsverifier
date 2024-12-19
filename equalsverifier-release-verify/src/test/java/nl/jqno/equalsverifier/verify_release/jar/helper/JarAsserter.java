package nl.jqno.equalsverifier.verify_release.jar.helper;

// CHECKSTYLE OFF: IllegalImport

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;

public class JarAsserter {

    public static final String EV = "/nl/jqno/equalsverifier";

    private final JarReader reader;

    public JarAsserter(JarReader reader) {
        this.reader = reader;
    }

    public void assertPresenceOfCoreClasses() {
        assertPresenceOf(
            EV + "/EqualsVerifier.class",
            EV + "/internal/reflection/ClassProbe.class",
            EV + "/internal/checkers/HierarchyChecker.class");
    }

    public void assertPresenceOfMultiReleaseClasses() {
        assertPresenceOf(
            "/META-INF/versions/11" + EV + "/internal/versionspecific/ModuleHelper.class",
            "/META-INF/versions/16" + EV + "/internal/versionspecific/RecordsHelper.class",
            "/META-INF/versions/17" + EV + "/internal/versionspecific/SealedTypesHelper.class",
            "/META-INF/versions/21" + EV + "/internal/versionspecific/SequencedCollectionsHelper.class");
    }

    public void assertPresenceOfEmbeddedDepedencies() {
        assertPresenceOf(
            EV + "/internal/lib/bytebuddy/ByteBuddy.class",
            EV + "/internal/lib/objenesis/Objenesis.class");
    }

    public void assertAbsenceOfEmbeddedDepedencies() {
        assertAbsenceOf(EV + "/internal/lib/bytebuddy/ByteBuddy.class", EV + "/internal/lib/objenesis/Objenesis.class");
    }

    public void assertPresenceOf(String... filenames) {
        var entries = reader.getEntries();
        assertForAll(
            entries::contains,
            fn -> "Expected presence of: " + fn + ", but it was absent.\nFilename: " + reader.getFilename(),
            filenames);
    }

    public void assertAbsenceOf(String... fileNames) {
        var entries = reader.getEntries();
        assertForAll(
            fn -> !entries.contains(fn),
            fn -> "Expected absence of: " + fn + ", but it was present.\nFilename: " + reader.getFilename(),
            fileNames);
    }

    private void assertForAll(Predicate<String> assertion, Function<String, String> message, String... filenames) {
        Stream<Executable> assertions =
                Arrays.stream(filenames).map(fn -> () -> assertThat(assertion.test(fn)).as(message.apply(fn)).isTrue());
        assertAll(assertions);
    }

    public void assertContentOfManifest(String implementationTitle) {
        var filename = "/META-INF/MANIFEST.MF";
        var manifest = new String(reader.getContentOf(filename));
        assertAll(
            () -> assertContains("Automatic-Module-Name: nl.jqno.equalsverifier", manifest, filename),
            () -> assertContains("Implementation-Title: " + implementationTitle, manifest, filename),
            () -> assertContains("Implementation-Version: ", manifest, filename),
            () -> assertContains("Multi-Release: true", manifest, filename),
            () -> assertContains("Website: https://www.jqno.nl/equalsverifier", manifest, filename));
    }

    private void assertContains(String needle, String haystack, String innerFilename) {
        assertThat(haystack.contains(needle)).as("Expected to find '" + needle + "' in " + innerFilename).isTrue();
    }

    public void assertVersionsOfClassFiles() {
        // See https://javaalmanac.io/bytecode/versions/
        assertAll(
            () -> assertVersionOfClassFile(52, EV + "/EqualsVerifier.class"),
            () -> assertVersionOfClassFile(
                55,
                "/META-INF/versions/11" + EV + "/internal/versionspecific/ModuleHelper.class"),
            () -> assertVersionOfClassFile(
                60,
                "/META-INF/versions/16" + EV + "/internal/versionspecific/RecordsHelper.class"),
            () -> assertVersionOfClassFile(
                61,
                "/META-INF/versions/17" + EV + "/internal/versionspecific/SealedTypesHelper.class"),
            () -> assertVersionOfClassFile(
                65,
                "/META-INF/versions/21" + EV + "/internal/versionspecific/SequencedCollectionsHelper.class"));
    }

    public void assertVersionsOfEmbeddedClassFiles() {
        assertAll(
            () -> assertVersionOfClassFile(49, EV + "/internal/lib/bytebuddy/ByteBuddy.class"),
            () -> assertVersionOfClassFile(52, EV + "/internal/lib/objenesis/Objenesis.class"));
    }

    private void assertVersionOfClassFile(int expectedVersion, String innerFilename) {
        var classFile = reader.getContentOf(innerFilename);
        var actualVersion = classFile[7];
        assertThat(actualVersion)
                .as(
                    "Expected " + innerFilename + " to have version " + expectedVersion + ", but it has version "
                            + actualVersion)
                .isEqualTo((byte) expectedVersion);
    }
}
