package lin_core;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

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
        // TODO check branch name
        // TODO implement
        return lastResultCommits;
    }

    public Pair squash(RevCommit start, RevCommit end) throws NullPointerException {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }

    public Pair stripCommitMessages(RevCommit start, RevCommit end) throws NullPointerException {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }

    public Pair fixCaseInCommitMessages(RevCommit start, RevCommit end) throws NullPointerException {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }

    public Pair removeStarsAndPlusesInCommitMessages(RevCommit start, RevCommit end) throws NullPointerException {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        List<RevCommit> commitsToFix = new LinkedList<>();
        for (RevCommit current = end; current != null && current != start; current = current.getParent(0)) { // TODO create walk method
            if (current.getFullMessage().startsWith("*") || current.getFullMessage().startsWith("+")) { // TODO check from list
                commitsToFix.add(current);
            }
            if (current.getParents() == null || current.getParents().length == 0) {
                break;
            }
        }
        for (RevCommit commit : commitsToFix) {
            // TODO find a way to rename commit
            System.out.println(commit.getFullMessage());
        }
        lastResultCommits = new Pair(start, end);
        return lastResultCommits;
    }

    public Pair fixBigCommitMessages(RevCommit start, RevCommit end) throws NullPointerException {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return lastResultCommits;
    }
}
