package org.wildfly.deptreediff.core;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SystemOutReporter implements DepTreeDiffReporter {
    @Override
    public void addNewDependency(String gav) {
        System.out.println("Added dependency: " + gav);
    }

    @Override
    public void addRemovedDependency(String gav) {
        System.out.println("Removed dependency: " + gav);
    }

    @Override
    public void addMajorVersionUpgrade(VersionChange change) {
        System.out.println("Major Upgrade: " + change.getOriginalGavString() + " -> " + change.getNewVersion());
    }

    @Override
    public void addMinorVersionUpgrade(VersionChange change) throws Exception {
        System.out.println("Minor Upgrade: " + change.getOriginalGavString() + " -> " + change.getNewVersion());
    }

    @Override
    public void addMicroVersionUpgrade(VersionChange change) throws Exception {
        System.out.println("Micro Upgrade: " + change.getOriginalGavString() + " -> " + change.getNewVersion());
    }

    @Override
    public void done() {

    }
}
