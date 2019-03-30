package org.wildfly.deptreediff.core;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class DependencyVersion {
    private final String version;
    private final List<String> versionParts;

    private DependencyVersion(String version, List<String> versionParts) {
        this.version = version;
        this.versionParts = versionParts;
    }

    String getVersion() {
        return version;
    }

    List<String> getVersionParts() {
        return versionParts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyVersion version1 = (DependencyVersion) o;

        if (!version.equals(version1.version)) return false;
        return versionParts.equals(version1.versionParts);
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + versionParts.hashCode();
        return result;
    }

    public boolean equalsMajorVersion(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyVersion version1 = (DependencyVersion) o;
        return versionParts.get(0).equals(version1.versionParts.get(0));
    }

    static DependencyVersion parseVersion(String version) {
        String[] parts = version.split("\\.");
        return new DependencyVersion(version, Arrays.asList(parts));
    }
}
