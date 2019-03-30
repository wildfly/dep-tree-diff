package org.wildfly.deptreediff.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
class DepTreeParser {
    private final BufferedReader reader;

    private final String PLUGIN_NAME = "maven-dependency-plugin";

    DepTreeParser(BufferedReader reader) {
        this.reader = reader;
    }

    List<Dependency> parse() throws IOException {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        // Read up to the plugin marker
        String line = reader.readLine();
        while (line != null) {
            if (line.contains(PLUGIN_NAME)) {
                break;
            }
            line = reader.readLine();
        }

        // Now we shuld have the dependencies
        line = reader.readLine();
        if (line != null) {
            // Skip the owning project
            line = reader.readLine();
        }
        while (line != null) {
            if (!line.contains(":")) {
                break;
            }

            int index = line.indexOf("-");
            String gavString = line.substring(index + 1).trim();
            if (gavString.contains("(")) {
                gavString = gavString.substring(0, gavString.indexOf("(")).trim();
            }
            if (gavString.length() > 0) {
                dependencies.add(Dependency.parseDependency(gavString));
            }
            line = reader.readLine();
        }
        return dependencies;
    }
}
