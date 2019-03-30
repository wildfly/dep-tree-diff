package org.wildfly.deptreediff.core;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class Dependency {

    private final String gavString;
    private final String groupId;
    private final String artifactId;
    private final String packaging;
    private final DependencyVersion version;
    private final String scope;
    private final String classifier;

    private Dependency(
            String gavString, String groupId, String artifactId,
            String packaging, DependencyVersion version, String scope, String classifier) {
        this.gavString = gavString;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.packaging = packaging;
        this.version = version;
        this.scope = scope;
        this.classifier = classifier;
    }

    String getGavString() {
        return gavString;
    }

    String getGroupId() {
        return groupId;
    }

    String getArtifactId() {
        return artifactId;
    }

    String getPackaging() {
        return packaging;
    }

    DependencyVersion getVersion() {
        return version;
    }

    String getScope() {
        return scope;
    }

    String getClassifier() {
        return classifier;
    }

    static Dependency parseDependency(String gavString) {

        String[] parts = gavString.split(":");
        if (parts.length < 4) {
            throw new IllegalStateException("Was not able to parseDependency: '" + gavString + "'");
        }
        String groupId = parts[0];
        String artifactId = parts[1];
        String packaging = parts[2];

        String version = null;
        String scope = null;
        String classifier = null;
        if (parts.length < 6) {
            version = parts[3];
            scope = parts.length == 4 ? null : parts[4];
        } else {
            classifier = parts[3];
            version = parts[4];
            scope = parts[5];
        }

        return new Dependency(gavString, groupId, artifactId, packaging, DependencyVersion.parseVersion(version), scope, classifier);
    }

}
