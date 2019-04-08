package org.wildfly.deptreediff.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class DepTreeDiffToolTest {
    @Test
    public void testNoChanges() throws Exception {

        TestDiffReporter reporter = runDiffTool(
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .build(),
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .build());

        Assert.assertEquals(0, reporter.addedDepedencies.size());
        Assert.assertEquals(0, reporter.removedDependecies.size());
        Assert.assertEquals(0, reporter.majorVersionChanges.size());
        Assert.assertTrue(reporter.doneCalled);
    }

    @Test
    public void testAddedDeps() throws Exception {
        TestDiffReporter reporter = runDiffTool(
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .build(),
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .addDependency("org.x:one:jar:1.2.3")
                        .addDependency("org.example:c:jar:1.2.3:compile")
                        .addDependency("org.example:c:jar:classy:1.2.3:compile")
                        .build());

        Assert.assertEquals(3, reporter.addedDepedencies.size());
        Assert.assertEquals("org.example:c:jar:1.2.3:compile", reporter.addedDepedencies.get(0));
        Assert.assertEquals("org.example:c:jar:classy:1.2.3:compile", reporter.addedDepedencies.get(1));
        Assert.assertEquals("org.x:one:jar:1.2.3", reporter.addedDepedencies.get(2));
        Assert.assertEquals(0, reporter.removedDependecies.size());
        Assert.assertEquals(0, reporter.majorVersionChanges.size());
        Assert.assertTrue(reporter.doneCalled);
    }

    @Test
    public void testRemovedDeps() throws Exception {
        TestDiffReporter reporter = runDiffTool(
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .addDependency("org.b:one:jar:1.2.3")
                        .addDependency("org.example:c:jar:1.2.3:compile")
                        .addDependency("org.example:c:jar:classy:1.2.3:compile")
                        .build(),
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .build());

        Assert.assertEquals(0, reporter.addedDepedencies.size());
        Assert.assertEquals(3, reporter.removedDependecies.size());
        Assert.assertEquals("org.b:one:jar:1.2.3", reporter.removedDependecies.get(0));
        Assert.assertEquals("org.example:c:jar:1.2.3:compile", reporter.removedDependecies.get(1));
        Assert.assertEquals("org.example:c:jar:classy:1.2.3:compile", reporter.removedDependecies.get(2));
        Assert.assertEquals(0, reporter.majorVersionChanges.size());
        Assert.assertTrue(reporter.doneCalled);
    }

    @Test
    public void testMajorVersionChanges() throws Exception {
        TestDiffReporter reporter = runDiffTool(
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.2.3")
                        .addDependency("org.example:two:jar:1.2.3:compile")
                        .addDependency("org.example:two:jar:classy:1.2.3:compile")
                        .addDependency("org.example:a:jar:1.2.3")
                        .addDependency("org.example:c:jar:1.2.3:compile")
                        .addDependency("org.example:c:jar:classy:1.2.3:compile")
                        .build(),
                new DependencyListBuilder()
                        .addDependency("org.example:one:jar:1.20.30")
                        .addDependency("org.example:two:jar:1.20.30:compile")
                        .addDependency("org.example:two:jar:classy:1.20.30:compile")
                        .addDependency("org.example:a:jar:2.2.3")
                        .addDependency("org.example:c:jar:10.2.3:compile")
                        .addDependency("org.example:c:jar:classy:3.2.3:compile")
                        .build());

        Assert.assertEquals(0, reporter.addedDepedencies.size());
        Assert.assertEquals(0, reporter.removedDependecies.size());
        Assert.assertEquals(3, reporter.majorVersionChanges.size());
        Assert.assertEquals("org.example:a:jar:1.2.3", reporter.majorVersionChanges.get(0).getOriginalGavString());
        Assert.assertEquals("2.2.3", reporter.majorVersionChanges.get(0).getNewVersion());
        Assert.assertEquals("org.example:c:jar:1.2.3:compile", reporter.majorVersionChanges.get(1).getOriginalGavString());
        Assert.assertEquals("10.2.3", reporter.majorVersionChanges.get(1).getNewVersion());
        Assert.assertEquals("org.example:c:jar:classy:1.2.3:compile", reporter.majorVersionChanges.get(2).getOriginalGavString());
        Assert.assertEquals("3.2.3", reporter.majorVersionChanges.get(2).getNewVersion());
        Assert.assertTrue(reporter.doneCalled);
    }

    private TestDiffReporter runDiffTool(List<Dependency> originalDeps, List<Dependency> newDeps) throws Exception {
        List<DepTreeDiffReporter> reporters = new ArrayList<>();
        reporters.add(new SystemOutReporter());
        TestDiffReporter testReporter = new TestDiffReporter();
        reporters.add(testReporter);

        DepTreeDiffTool tool = new DepTreeDiffTool(reporters, originalDeps, newDeps);
        tool.reportDiffs();

        return testReporter;
    }

    private static class DependencyListBuilder {
        List<Dependency> dependencies = new ArrayList<>();

        DependencyListBuilder addDependency(String gavString) {
            dependencies.add(Dependency.parseDependency(gavString));
            return this;
        }

        List<Dependency> build() {
            return dependencies;
        }
    }
}
