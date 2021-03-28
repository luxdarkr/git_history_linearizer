package git_lin;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Dictionary;

public class Linearizer {
    Repository activeRepo = null;
    Pair<RevCommit, RevCommit> lastResultCommits = null;

    Linearizer(Repository repo) {
        activeRepo = repo;
    }

    public Pair getLastResultCommits() throws NullPointerException {
        return lastResultCommits;
    }

    public void changeRepo(Repository repo) throws NullPointerException {
        if (repo == null) {
            throw new NullPointerException();
        }
        activeRepo = repo;
        lastResultCommits = null;
    }

    public Pair linearize(RevCommit start, RevCommit end) throws Exception {
        String branchName = "";
        // TODO: build branch based on repo head name
        branchName = "result";
        return linearize(start, end, branchName);
    }

    public Pair linearize(RevCommit start, RevCommit end, String newBranchName) throws Exception {
        if (start == null || end == null || newBranchName == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }

    public Pair squash(RevCommit start, RevCommit end) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }

    public Pair renameCommits(RevCommit start, RevCommit end, Dictionary<String, String> dict) throws NullPointerException {
        if (start == null || end == null || dict == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }
}
