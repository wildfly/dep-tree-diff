package org.wildfly.deptreediff.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class TestDiffReporter implements DepTreeDiffReporter {
    List<String> addedDepedencies = new ArrayList<>();
    List<String> removedDependecies = new ArrayList<>();
    List<VersionChange> majorVersionChanges = new ArrayList<>();
    boolean doneCalled;

    @Override
    public void addNewDependency(String gav) {
        addedDepedencies.add(gav);
    }

    @Override
    public void addRemovedDependency(String gav) {
        removedDependecies.add(gav);
    }

    @Override
    public void addMajorVersionUpgrade(VersionChange majorVersionChange) {
        majorVersionChanges.add(majorVersionChange);
    }

    @Override
    public void done() {
        doneCalled = true;
    }
}

