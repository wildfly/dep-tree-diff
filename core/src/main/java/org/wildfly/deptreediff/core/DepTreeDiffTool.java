package org.wildfly.deptreediff.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class DepTreeDiffTool {

    private final List<DepTreeDiffReporter> reporters;
    private final Map<DependencyKey, Dependency> originalDeps;
    private final Map<DependencyKey, Dependency> newDeps;

    DepTreeDiffTool(List<DepTreeDiffReporter> reporters, Map<DependencyKey, Dependency> originalDeps, Map<DependencyKey, Dependency> newDeps) {
        this.reporters = reporters;
        this.originalDeps = Collections.unmodifiableMap(originalDeps);
        this.newDeps = Collections.unmodifiableMap(newDeps);
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

    private List<Dependency> findAddedDependencies(Map<DependencyKey, Dependency> originalDeps, Map<DependencyKey, Dependency> newDeps) {
        return findOnlyInLeft(newDeps, originalDeps);
    }

    private List<Dependency> findRemovedDependencies(Map<DependencyKey, Dependency> originalDeps, Map<DependencyKey, Dependency> newDeps) {
        return findOnlyInLeft(originalDeps, newDeps);
    }

    private List<MajorVersionChange> findMajorVersionChanges(Map<DependencyKey, Dependency> originalDeps, Map<DependencyKey, Dependency> newDeps) {
        List<MajorVersionChange> majorVersionChanges = new ArrayList<>();
        for (DependencyKey newKey : newDeps.keySet()) {
            Dependency originalDep = originalDeps.get(newKey);
            if (originalDep == null) {
                continue;
            }
            Dependency newDep = newDeps.get(newKey);
            if (!newDep.getVersion().equalsMajorVersion(originalDep.getVersion())) {
                majorVersionChanges.add(new MajorVersionChange(originalDep, newDep));
            }
        }

        majorVersionChanges.sort(Comparator.comparing(MajorVersionChange::getOriginalGavString));
        return majorVersionChanges;
    }

    private List<Dependency> findOnlyInLeft(Map<DependencyKey, Dependency> left, Map<DependencyKey, Dependency> right) {
        List<Dependency> additions = new ArrayList<>();

        for (DependencyKey key : left.keySet()) {
            if (right.get(key) == null) {
                additions.add(left.get(key));
            }
        }
        additions.sort(Comparator.comparing(Dependency::getGavString));
        return additions;
    }

    public static DepTreeDiffTool create(List<File> originalFiles, List<File> newFiles) throws IOException {
        List<DepTreeDiffReporter> reporters = new ArrayList<>();
        reporters.add(new SystemOutReporter());

        ServiceLoader<DepTreeDiffReporter> serviceLoader = ServiceLoader.load(DepTreeDiffReporter.class);
        for (Iterator<DepTreeDiffReporter> it = serviceLoader.iterator(); it.hasNext(); ) {
            DepTreeDiffReporter reporter = it.next();
            reporters.add(reporter);
        }

        Map<DependencyKey, Dependency> originalDeps = parseDependencies(originalFiles);
        Map<DependencyKey, Dependency> newDeps = parseDependencies(newFiles);



        return new DepTreeDiffTool(reporters, originalDeps, newDeps);
    }

    private static Map<DependencyKey, Dependency> parseDependencies(List<File> files) throws IOException {
        Map<DependencyKey, Dependency> allDependencies = new LinkedHashMap<>();
        Map<File, Map<DependencyKey, Dependency>> depsByFile = new LinkedHashMap<>();
        for (File file : files) {

            Map<DependencyKey, Dependency> depsForFile = new HashMap<>();
            List<File> reverseKeys = new ArrayList<>(depsByFile.keySet());
            Collections.reverse(reverseKeys);

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                new DepTreeParser(reader).parse().forEach(d -> {
                    DependencyKey key = new DependencyKey(d);
                    if (allDependencies.put(key, d) != null) {
                        // Find the already added one and warn
                        for (File alreadyParsed : reverseKeys) {
                            Dependency existing = depsByFile.get(alreadyParsed).get(key);
                            if (existing != null) {
                                System.out.println(
                                        "WARN - '" + d.getGavString() + "' in '" + file + " was already found as '" +
                                        existing + "' in '" + alreadyParsed + "'");
                            }
                        }
                    }
                    depsForFile.put(key, d);
                });
            }
            depsByFile.put(file, depsForFile);
        }
        return allDependencies;
    }

}
