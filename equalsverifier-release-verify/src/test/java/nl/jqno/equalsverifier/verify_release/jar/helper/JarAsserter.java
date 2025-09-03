package nl.jqno.equalsverifier.verify_release.jar.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.util.Set;

public class JarAsserter {

    public static final String EV = "/nl/jqno/equalsverifier";
    public static final String LIB = EV + "/internal/lib";

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
            "/META-INF/versions/21" + EV + "/internal/versionspecific/SequencedCollectionsHelper.class",
            "/META-INF/versions/25" + EV + "/internal/versionspecific/ScopedValuesHelper.class");
    }

    public void assertAbsenceOfMetainfStuff() {
        assertAbsenceOf("/META-INF/LICENSE", "/META-INF/maven/nl.jqno.equalsverifier/equalsverifier-core/pom.xml");
    }

    public void assertAbsenceOfTesthelpers() {
        assertAbsenceOf(EV + "-testhelpers/Util.class", EV + "-testhelpers/ExpectedException.class");
    }

    public void assertPresenceOfEmbeddedDepedencies() {
        assertPresenceOf(LIB + "/bytebuddy/ByteBuddy.class", LIB + "/objenesis/Objenesis.class");
    }

    public void assertAbsenceOfEmbeddedDepedencies() {
        assertAbsenceOf(LIB + "/bytebuddy/ByteBuddy.class", LIB + "/objenesis/Objenesis.class");
    }

    public void assertAbsenceOfEmbeddedDepedenciesUnderTheirOriginalPath() {
        assertAbsenceOf("/net/bytebuddy/ByteBuddy.class", "/org/objenesis/Objenesis.class");
    }

    public void assertAbsenceOfOptionalDependencies() {
        assertAbsenceOf(
            "/org/mockito/Mockito.class",
            LIB + "/org/mockito/Mockito.class",
            LIB + "/mockito/Mockito.class");
    }

    public void assertPresenceOf(String... filenames) {
        var entries = reader.getEntries();
        assertThat(filenames).allMatch(entries::contains, "present in " + reader.getFilename());
    }

    public void assertAbsenceOf(String... fileNames) {
        var entries = reader.getEntries();
        assertThat(fileNames).allMatch(fn -> !entries.contains(fn), "absent from " + reader.getFilename());
    }

    public void assertAbsenceOfDirectory(String... dirNames) {
        var dirs = Set.of(dirNames);
        var entries = reader.getEntries();
        assertThat(entries).noneMatch(dirs::contains);
    }

    public void assertContentOfManifest(String implementationTitle) {
        var filename = "/META-INF/MANIFEST.MF";
        var manifest = new String(reader.getContentOf(filename), Charset.defaultCharset());
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
                    b -> assertVersionOfClassFile(61, EV + "/EqualsVerifier.class"),
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

    public void assertModuleInfoWithDependencies() {
        var moduleinfo = reader.getContentOf("module-info.class");
        var module = ModuleInfoAsserter.parse(moduleinfo);
        assertThat(module)
                .satisfies(
                    m -> m.assertName("nl.jqno.equalsverifier"),
                    m -> m.assertExports("nl/jqno/equalsverifier", "nl/jqno/equalsverifier/api"),
                    m -> m.assertRequires("net.bytebuddy", "transitive"),
                    m -> m.assertRequires("org.objenesis", ""));
    }

    public void assertModuleInfoWithoutDependencies() {
        var moduleinfo = reader.getContentOf("module-info.class");
        var module = ModuleInfoAsserter.parse(moduleinfo);
        assertThat(module)
                .satisfies(
                    m -> m.assertName("nl.jqno.equalsverifier"),
                    m -> m.assertExports("nl/jqno/equalsverifier", "nl/jqno/equalsverifier/api"),
                    m -> m.assertDoesNotRequire("net.bytebuddy"),
                    m -> m.assertDoesNotRequire("org.objenesis"));
    }
}
