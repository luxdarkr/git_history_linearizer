package lin_core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static lin_core.Linearizer.openRepo;

public class LinearizerTest {

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

    Path getTestPath() {
        return Paths
            .get(System.getProperty("user.dir"))
            .getParent()
            .resolve("tests");
    }

    void testLinearizerOnRepository(String repoPath, String refName, String startCommitId) throws Exception {
        // Path to the "tests" folder
        Path testPath = getTestPath();

        // Path to repo
        Path repoDir = testPath.resolve(repoPath);
        // Reset repo
        resetTestsFolder(repoDir.getParent().toFile());

        // Linearizer params
        String[] emptyParams = new String[0];
        Map<String, String[]> settings = new TreeMap<>();

        // Linearize repo
        Linearizer.processRepo(
                repoDir.toString(),
                refName,
                startCommitId,
                settings
        );

        Repository repo = Linearizer.openRepo(repoDir.toString());
        Git git = new Git(repo);
        RevWalk walk = new RevWalk(repo);
        Iterable<RevCommit> commits = git.log().call();
        Ref head = repo.findRef(refName);
        File repoRootDir = repoDir.getParent().toFile();


        RevCommit headCommit = walk.parseCommit(head.getObjectId());
        RevCommit startCommit = walk.parseCommit(ObjectId.fromString(startCommitId));

        String linearizedDirHash = Hashing.hashDirectory(repoRootDir, true);
        executeCommand(repoRootDir, "git", "checkout", "master");
        String masterDirHash = Hashing.hashDirectory(repoRootDir, true);
        assert(linearizedDirHash.equals(masterDirHash));

        List<RevCommit> commitsToLinearize = Linearizer.getOrder(walk, headCommit, startCommit, git);
        head = repo.findRef("refs/heads/linearizer_work");

        RevCommit commit = walk.parseCommit(head.getObjectId());
        int ncommits = 0;


        while (!commit.equals(startCommit)) {
            RevCommit parentCommit = walk.parseCommit(commit.getId());
            RevCommit[] parents = parentCommit.getParents();
            assert(parents.length == 1);
            commit = parents[0];
            ncommits++;
        }

        assert(ncommits == commitsToLinearize.size());
    }

    @Test
    public void linearizeRepoSimple() throws Exception {
        Path t = getTestPath().resolve("datasets/git_test_simple");
        if (!Files.exists(t)) {
            executeCommand(t.getParent().toFile(), "git", "clone", "https://github.com/luxdarkr/git_test_simple.git");
        }

        testLinearizerOnRepository(
                "datasets/git_test_simple/.git",
                "refs/heads/master",
                "4c5568af4b07a41aa22f0fac200ed9af6b5e09ad"
        );
        testLinearizerOnRepository(
                "datasets/git_test_simple/.git",
                "refs/heads/master",
                "eb3884537613d77d41e0f7575bedbcb00e19a6fc"
        );
    }

    /*@Test
    public void linearizeTechnoEvents() throws Exception {
        Path t = getTestPath().resolve("datasets/TechnoEvents");
        if (!Files.exists(t)) {
            executeCommand(t.getParent().toFile(), "git", "clone", "https://github.com/Hiraev/TechnoEvents.git");
        }

        testLinearizerOnRepository(
                "datasets/TechnoEvents/.git",
                "refs/heads/master",
                "58674c1e220890b1069156b0774c9cecf294480d"
        );
    }*/

    /*@Test
    public void linearizeSyto() throws Exception {
        Path t = getTestPath().resolve("datasets/syto");
        if (!Files.exists(t)) {
            executeCommand(t.getParent().toFile(), "git", "clone", "https://github.com/SashkoTar/syto");
        }

        testLinearizerOnRepository(
                "datasets/syto/.git",
                "refs/heads/master",
                "de05e2246717366bb0af2b4e44b988d0f871ec1f"
        );
    }*/

    /*@Test
    public void linearizeModbusRS() throws Exception {
        Path t = getTestPath().resolve("datasets/modbus-rs");
        if (!Files.exists(t)) {
            executeCommand(t.getParent().toFile(), "git", "clone", "https://github.com/hirschenberger/modbus-rs.git");
        }

        testLinearizerOnRepository(
                "datasets/modbus-rs/.git",
                "refs/heads/master",
                "5aa5db782d8a90c1e2d3934bfaf05abd3a5a6863"
        );
    }*/
}
