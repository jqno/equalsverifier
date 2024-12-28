package nl.jqno.equalsverifier.verify_release.jar.helper;

import static org.assertj.core.api.Assertions.assertThat;

public class JarAsserter {

    public static final String EV = "/nl/jqno/equalsverifier";

    private final JarReader reader;

    public JarAsserter(JarReader reader) {
        this.reader = reader;
    }

    public void assertPresenceOfCoreClasses() {
        assertPresenceOf(
            "/module-info.class",
            EV + "/EqualsVerifier.class",
            EV + "/internal/reflection/ClassProbe.class",
            EV + "/internal/checkers/HierarchyChecker.class");
    }

    public void assertPresenceOfMultiReleaseClasses() {
        assertPresenceOf(
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
        assertThat(filenames).allMatch(entries::contains, "present in " + reader.getFilename());
    }

    public void assertAbsenceOf(String... fileNames) {
        var entries = reader.getEntries();
        assertThat(fileNames).allMatch(fn -> !entries.contains(fn), "absent from " + reader.getFilename());
    }

    public void assertContentOfManifest(String implementationTitle) {
        var filename = "/META-INF/MANIFEST.MF";
        var manifest = new String(reader.getContentOf(filename));
        assertThat(manifest)
                .satisfies(
                    m -> assertContains("Implementation-Title: " + implementationTitle, m, filename),
                    m -> assertContains("Implementation-Version: ", m, filename),
                    m -> assertContains("Multi-Release: true", m, filename),
                    m -> assertContains("Website: https://www.jqno.nl/equalsverifier", m, filename));
    }

    private void assertContains(String needle, String haystack, String innerFilename) {
        assertThat(haystack).as("Expected to find '" + needle + "' in " + innerFilename).contains(needle);
    }

    public void assertVersionsOfClassFiles() {
        // See https://javaalmanac.io/bytecode/versions/
        assertThat(true)
                .satisfies(
                    b -> assertVersionOfClassFile(55, EV + "/EqualsVerifier.class"),
                    b -> assertVersionOfClassFile(
                        60,
                        "/META-INF/versions/16" + EV + "/internal/versionspecific/RecordsHelper.class"),
                    b -> assertVersionOfClassFile(
                        61,
                        "/META-INF/versions/17" + EV + "/internal/versionspecific/SealedTypesHelper.class"),
                    b -> assertVersionOfClassFile(
                        65,
                        "/META-INF/versions/21" + EV + "/internal/versionspecific/SequencedCollectionsHelper.class"));
    }

    public void assertVersionsOfEmbeddedClassFiles() {
        assertThat(true)
                .satisfies(
                    b -> assertVersionOfClassFile(49, EV + "/internal/lib/bytebuddy/ByteBuddy.class"),
                    b -> assertVersionOfClassFile(52, EV + "/internal/lib/objenesis/Objenesis.class"));
    }

    private void assertVersionOfClassFile(int expectedVersion, String innerFilename) {
        var classFile = reader.getContentOf(innerFilename);
        var actualVersion = classFile[7];
        var description = "Expected " + innerFilename + " to have version " + expectedVersion + ", but it has version "
                + actualVersion;
        assertThat(actualVersion).as(description).isEqualTo((byte) expectedVersion);
    }
}
