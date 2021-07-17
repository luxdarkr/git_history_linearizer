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
        String refName = "refs/heads/master";
        String startCommitId = "4c5568af4b07a41aa22f0fac200ed9af6b5e09ad";
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
        RevCommit newHeadCommit = walk.parseCommit(head.getObjectId());

        RevCommit commit = newHeadCommit;
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
}
