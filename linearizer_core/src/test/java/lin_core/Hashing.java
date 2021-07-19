package lin_core;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;


public class Hashing {
    public static String hashDirectory(File directory, boolean includeHiddenFiles) throws IOException {

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }

        Vector<Stream<String>> fileStreams = new Vector<>();
        collectFiles(directory, fileStreams, includeHiddenFiles);

        String res = "";
        for (Stream<String> fs : fileStreams) {
            StringBuilder contentBuilder = new StringBuilder();

            try (Stream<String> stream = fs) {
                stream.forEach(s -> contentBuilder.append(s).append("\n"));
            }
            res += contentBuilder.toString();
        }
        return res;
        //try (SequenceInputStream sequenceInputStream = new SequenceInputStream(fileStreams.elements())) {
        //    return res;
        //}
    }

    private static void collectFiles(File directory, List<Stream<String>> fileInputStreams,
                                     boolean includeHiddenFiles) throws IOException {
        File[] files = directory.listFiles();

        for (File f : files) {
            if (f.getName().startsWith("."))
                files = ArrayUtils.removeElement(files, f);

        }

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));

            for (File file : files) {
                if (includeHiddenFiles || !Files.isHidden(file.toPath())) {
                    if (file.isDirectory()) {
                        collectFiles(file, fileInputStreams, includeHiddenFiles);
                    } else {
                        fileInputStreams.add(Files.lines( Paths.get(file.getPath()), StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }
}