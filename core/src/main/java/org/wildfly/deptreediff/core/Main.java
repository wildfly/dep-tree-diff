package org.wildfly.deptreediff.core;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class Main {
    public static void main(String[] args) throws Exception  {
        if (args.length != 2) {
            throw new IllegalStateException("Usage: <original file path> <changed file path>");
        }

        File originalFile = new File(args[0]);
        File newFile = new File(args[1]);
        if (!originalFile.exists()) {
            throw new IllegalStateException("Original file does not exist: " + args[0]);
        }
        if (!newFile.exists()) {
            throw new IllegalStateException("New file does not exist: " + args[1]);
        }
        final DepTreeDiffTool tool;
        BufferedReader originalReader = new BufferedReader(new FileReader(originalFile));
        try {
            BufferedReader newReader = new BufferedReader(new FileReader(newFile));
            try {
                tool = DepTreeDiffTool.create(originalReader, newReader);
            } finally {
                close(newReader);
            }
        } finally {
            close(originalReader);
        }

        tool.reportDiffs();
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ignore) {

        }
    }
}
