package nl.jqno.equalsverifier.verify_release.pom.helper;

import static org.assertj.core.api.Assertions.assertThat;

public class PomAsserter {

    private final PomReader reader;

    public PomAsserter(PomReader reader) {
        this.reader = reader;
    }

    public void assertCommonProperties() {
        assertThat(reader)
                .satisfies(
                    r -> r.assertNode("/project/groupId", "nl.jqno.equalsverifier"),
                    r -> r.assertNode("/project/url", "https://www.jqno.nl/equalsverifier"),
                    r -> r.assertNode("/project/inceptionYear", "2009"),
                    r -> r.assertNode("/project/licenses/license[1]/name", "Apache License, Version 2.0"),
                    r -> r.assertNode("/project/developers/developer[1]/name", "Jan Ouwens"),
                    r -> r
                            .assertNode(
                                "/project/mailingLists/mailingList[1]/archive",
                                "https://groups.google.com/group/equalsverifier"),
                    r -> r.assertNode("/project/scm/url", "https://github.com/jqno/equalsverifier"),
                    r -> r.assertNode("/project/issueManagement/url", "https://github.com/jqno/equalsverifier/issues"),
                    r -> r.assertNode("/project/ciManagement/url", "https://github.com/jqno/equalsverifier/actions"));
    }

    public void assertArtifactId(String artifactId) {
        reader.assertNode("/project/artifactId", artifactId);
    }

    public void assertNumberOfDependencies(int n) {
        reader.assertNodeSize(n, "/project/dependencies/dependency");
    }

    public void assertDependency(int idx, String groupId, String artifactId) {
        var prefix = "/project/dependencies/dependency[" + idx + "]";
        assertThat(reader)
                .satisfies(
                    r -> r.assertNode(prefix + "/groupId", groupId),
                    r -> r.assertNode(prefix + "/artifactId", artifactId),
                    r -> r.assertNode(prefix + "/scope", "compile"));
    }

    public void assertDependencyIsOptional(int idx) {
        var prefix = "/project/dependencies/dependency[" + idx + "]";
        assertThat(reader)
                .satisfies(
                    r -> r.assertNode(prefix + "/optional", "true"),
                    r -> r.assertNode(prefix + "/scope", "provided"));
    }
}
