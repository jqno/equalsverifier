package nl.jqno.equalsverifier.verify_release.pom.helper;

import static org.junit.jupiter.api.Assertions.assertAll;

public class PomAsserter {

    private final PomReader reader;

    public PomAsserter(PomReader reader) {
        this.reader = reader;
    }

    public void assertCommonProperties() {
        assertAll(
            () -> reader.assertNode("/project/groupId", "nl.jqno.equalsverifier"),
            () -> reader.assertNode("/project/url", "https://www.jqno.nl/equalsverifier"),
            () -> reader.assertNode("/project/inceptionYear", "2009"),
            () ->
                reader.assertNode(
                    "/project/licenses/license[1]/name",
                    "Apache License, Version 2.0"
                ),
            () -> reader.assertNode("/project/developers/developer[1]/name", "Jan Ouwens"),
            () ->
                reader.assertNode(
                    "/project/mailingLists/mailingList[1]/archive",
                    "https://groups.google.com/group/equalsverifier"
                ),
            () -> reader.assertNode("/project/scm/url", "https://github.com/jqno/equalsverifier"),
            () ->
                reader.assertNode(
                    "/project/issueManagement/url",
                    "https://github.com/jqno/equalsverifier/issues"
                ),
            () ->
                reader.assertNode(
                    "/project/ciManagement/url",
                    "https://github.com/jqno/equalsverifier/actions"
                )
        );
    }

    public void assertArtifactId(String artifactId) {
        reader.assertNode("/project/artifactId", artifactId);
    }

    public void assertNumberOfDependencies(int n) {
        reader.assertNodeSize(n, "/project/dependencies/dependency");
    }

    public void assertDependency(int idx, String groupId, String artifactId) {
        var prefix = "/project/dependencies/dependency[" + idx + "]";
        assertAll(
            () -> reader.assertNode(prefix + "/groupId", groupId),
            () -> reader.assertNode(prefix + "/artifactId", artifactId),
            () -> reader.assertNode(prefix + "/scope", "compile")
        );
    }

    public void assertDependencyIsOptional(int idx) {
        var prefix = "/project/dependencies/dependency[" + idx + "]";
        assertAll(
            () -> reader.assertNode(prefix + "/optional", "true"),
            () -> reader.assertNode(prefix + "/scope", "provided")
        );
    }
}
