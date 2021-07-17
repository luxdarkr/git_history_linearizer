package lin_core;

import org.junit.Test;

import javax.print.attribute.standard.PresentationDirection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class CoreTest {

    void executeCommand(File location, String ... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(location);
        Process pr = pb.start();
        pr.waitFor();
    }

    // Delete all linearizer work
    // and return to master branch
    void resetTestsFolder(File testsPath) throws IOException, InterruptedException {
        executeCommand(testsPath, "git", "checkout", "master");
        executeCommand(testsPath, "git", "branch", "-D", "linearizer_work");
    }

    @Test
    public void linearizeRepoSimple() throws Exception {
        // Path to the "tests" folder
        Path testsPath = Paths.get(System.getProperty("user.dir"));
        testsPath = testsPath.getParent().resolve("tests");

        // Path to repo
        Path repoDir = testsPath.resolve("git_test_simple/.git");
        // Reset repo
        resetTestsFolder(repoDir.getParent().toFile());

        // Linearizer params
        String[] emptyParams = new String[0];
        Map<String, String[]> settings = new TreeMap<>();
        settings.put("badStarts", new String[] {"*", "+"});
        settings.put("strip", emptyParams);
        settings.put("fixCase", emptyParams);

        // Linearize repo
        Linearizer.processRepo(
            repoDir.toString(),
            "refs/heads/master",
            "4c5568af4b07a41aa22f0fac200ed9af6b5e09ad",
            settings
        );

        // TODO check result here
    }
}
