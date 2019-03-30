package org.wildfly.deptreediff.core;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class MajorVersionChange {
    private final String gavString;
    private final String toVersion;

    public MajorVersionChange(Dependency original, Dependency changed) {
        this.gavString = original.getGavString();
        this.toVersion = changed.getVersion().getVersion();
    }

    String getOriginalGavString() {
        return gavString;
    }

    String getNewVersion() {
        return toVersion;
    }
}
