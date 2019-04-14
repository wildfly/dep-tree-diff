package org.wildfly.deptreediff.core;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class DependencyKey {
    private final String groupId;
    private final String artifactId;
    private final String packaging;
    private final String scope;
    private final String classifier;


    public DependencyKey(Dependency dependency) {
        this.groupId = dependency.getGroupId();
        this.artifactId = dependency.getArtifactId();
        this.packaging = dependency.getPackaging();
        this.scope = dependency.getScope();
        this.classifier = dependency.getClassifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyKey that = (DependencyKey) o;

        if (!groupId.equals(that.groupId)) {
            return false;
        }
        if (!artifactId.equals(that.artifactId)) {
            return false;
        }
        if (scope != null) {
            if (!scope.equals(that.scope)) {
                return false;
            }
        } else {
            if (that.scope != null) {
                return false;
            }
        }
        if (classifier != null) {
            if (!classifier.equals(that.classifier)) {
                return false;
            }
        } else {
            if (that.classifier != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + artifactId.hashCode();
        if (scope != null) {
            result = 31 * result + scope.hashCode();
        }
        if (classifier != null) {
            result = 31 * result + classifier.hashCode();
        }
        return result;
    }
}
