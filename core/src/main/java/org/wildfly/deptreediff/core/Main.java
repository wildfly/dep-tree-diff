package org.wildfly.deptreediff.core;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class Main {
    public static void main(String[] args) throws Exception  {
        if (args.length != 2) {
            throw new IllegalStateException("Usage: <original file path> <changed file path>");
        }

        List<File> originalFiles = getFiles(args[0]);
        List<File> newFiles = getFiles(args[1]);

        final DepTreeDiffTool tool = DepTreeDiffTool.create(originalFiles, newFiles);

        tool.reportDiffs();
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ignore) {

        }
    }

    private static List<File> getFiles(String input) {
        List<File> files = new ArrayList<>();
        for (String s : input.split(",")) {
            File f = new File(s);
            if (!f.exists()) {
                throw new IllegalStateException("File '" + f + " does not exist");
            }
        }
        return files;
    }
}
