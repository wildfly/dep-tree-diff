package org.wildfly.deptreediff.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class DepTreeDiffTool {

    private final List<DepTreeDiffReporter> reporters;
    private final List<Dependency> originalDeps;
    private final List<Dependency> newDeps;

    DepTreeDiffTool(List<DepTreeDiffReporter> reporters, List<Dependency> originalDeps, List<Dependency> newDeps) {
        this.reporters = reporters;
        this.originalDeps = originalDeps;
        this.newDeps = newDeps;
    }

    void reportDiffs() throws Exception {
        List<Dependency> added = findAddedDependencies(originalDeps, newDeps);
        List<Dependency> removed = findRemovedDependencies(originalDeps, newDeps);
        List<MajorVersionChange> majorVersionChanges = findMajorVersionChanges(originalDeps, newDeps);

        for (DepTreeDiffReporter reporter : reporters) {
            for (Dependency dep : added) {
                reporter.addNewDependency(dep.getGavString());
            }
            for (Dependency dep : removed) {
                reporter.addRemovedDependency(dep.getGavString());
            }
            for (MajorVersionChange cmv : majorVersionChanges) {
                reporter.addMajorVersionUpgrade(cmv);
            }
            reporter.done();
        }

    }

    private List<Dependency> findAddedDependencies(List<Dependency> originalDeps, List<Dependency> newDeps) {
        return findOnlyInLeft(newDeps, originalDeps);
    }

    private List<Dependency> findRemovedDependencies(List<Dependency> originalDeps, List<Dependency> newDeps) {
        return findOnlyInLeft(originalDeps, newDeps);
    }

    private List<MajorVersionChange> findMajorVersionChanges(List<Dependency> originalDeps, List<Dependency> newDeps) {
        Map<DependencyKey, Dependency> map = new HashMap<>();
        for (Dependency dependency : originalDeps) {
            map.put(new DependencyKey(dependency), dependency);
        }


        List<MajorVersionChange> majorVersionChanges = new ArrayList<>();
        for (Dependency newDep : newDeps) {
            Dependency originalDep = map.get(new DependencyKey(newDep));
            if (originalDep == null) {
                continue;
            }
            if (!newDep.getVersion().equalsMajorVersion(originalDep.getVersion())) {
                majorVersionChanges.add(new MajorVersionChange(originalDep, newDep));
            }
        }

        majorVersionChanges.sort(Comparator.comparing(MajorVersionChange::getOriginalGavString));
        return majorVersionChanges;
    }

    private List<Dependency> findOnlyInLeft(List<Dependency> left, List<Dependency> right) {
        List<Dependency> additions = new ArrayList<>();
        Map<DependencyKey, Dependency> map = new HashMap<>();
        for (Dependency dependency : right) {
            map.put(new DependencyKey(dependency), dependency);
        }

        for (Dependency dependency : left) {
            if (map.get(new DependencyKey(dependency)) == null) {
                additions.add(dependency);
            }
        }
        additions.sort(Comparator.comparing(Dependency::getGavString));
        return additions;
    }

    public static DepTreeDiffTool create(BufferedReader originalReader, BufferedReader newReader) throws IOException {
        List<DepTreeDiffReporter> reporters = new ArrayList<>();
        reporters.add(new SystemOutReporter());

        ServiceLoader<DepTreeDiffReporter> serviceLoader = ServiceLoader.load(DepTreeDiffReporter.class);
        for (Iterator<DepTreeDiffReporter> it = serviceLoader.iterator(); it.hasNext(); ) {
            DepTreeDiffReporter reporter = it.next();
            reporters.add(reporter);
        }

        List<Dependency> originalDeps = new DepTreeParser(originalReader).parse();
        List<Dependency> newDeps = new DepTreeParser(newReader).parse();



        return new DepTreeDiffTool(reporters, originalDeps, newDeps);
    }

    private static class DependencyKey {
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
}
