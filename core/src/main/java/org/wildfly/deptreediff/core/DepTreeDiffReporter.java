package org.wildfly.deptreediff.core;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public interface DepTreeDiffReporter {
    void addNewDependency(String gav);

    void addRemovedDependency(String gav);

    void addMajorVersionUpgrade(MajorVersionChange change);

    void done();
}
