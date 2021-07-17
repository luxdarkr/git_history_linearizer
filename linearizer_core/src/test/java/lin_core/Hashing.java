package lin_core;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;


public class Hashing {
    public static String hashDirectory(File directory, boolean includeHiddenFiles) throws IOException {

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }

        Vector<FileInputStream> fileStreams = new Vector<>();
        collectFiles(directory, fileStreams, includeHiddenFiles);

        try (SequenceInputStream sequenceInputStream = new SequenceInputStream(fileStreams.elements())) {
            return DigestUtils.md5Hex(sequenceInputStream);
        }
    }

    private static void collectFiles(File directory, List<FileInputStream> fileInputStreams,
                                     boolean includeHiddenFiles) throws IOException {
        File[] files = directory.listFiles();

        for (File f : files) {
            if (f.getName().equals(".git"))
                files = ArrayUtils.removeElement(files, f);
        }

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));

            for (File file : files) {
                if (includeHiddenFiles || !Files.isHidden(file.toPath())) {
                    if (file.isDirectory()) {
                        collectFiles(file, fileInputStreams, includeHiddenFiles);
                    } else {
                        fileInputStreams.add(new FileInputStream(file));
                    }
                }
            }
        }
    }
}