package org.wildfly.deptreediff.core;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface DepTreeDiffReporter {
    void addNewDependency(String gav) throws Exception;

    void addRemovedDependency(String gav) throws Exception;

    void addMajorVersionUpgrade(VersionChange change) throws Exception;

    default void addMinorVersionUpgrade(VersionChange change) throws Exception {}

    default void addMicroVersionUpgrade(VersionChange change) throws Exception {}

    void done() throws Exception;
}
