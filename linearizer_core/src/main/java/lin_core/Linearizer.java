package lin_core;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.*;

public class Linearizer {
    public static CommitPair processRepo(Repository repo, RevCommit start, Map<String, String[]> settings) throws Exception { // TODO better method name and args
        if (repo == null || start == null || settings == null) {
            throw new NullPointerException();
        }
        CommitMessages messages = new CommitMessages();
        // TODO walk in repo and find all commits from start
        // it seems like JGit doesn't support direct child getting

        // test code start
        messages.set(start, "*** message");
        // test code end

        if (settings.containsKey("badStarts")) {
            messages = strip(removeBadStarts(messages, settings.get("badStarts")));
        }

        // test code start
        System.out.println("Commit messages after processing:");
        messages.apply((String s) -> { // test output
            System.out.println(s);
            return s;
        });
        // test code end
        return new CommitPair(null, null);
    }

    private static CommitPair linearize(RevCommit start, RevCommit end) throws Exception {
        String branchName = "";
        // TODO build branch based on repo head name
        branchName = "result";
        return linearize(start, end, branchName);
    }

    private static CommitPair linearize(RevCommit start, RevCommit end, String newBranchName) throws Exception {
        if (start == null || end == null || newBranchName == null) {
            throw new NullPointerException();
        }
        // TODO check branch name
        // TODO implement
        return new CommitPair(start, end);
    }

    /*
    // TODO consider when to use: at linearization time (not obvious) or after (slow)
    private static CommitMessages squash(CommitMessages messages) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return messages;
    }
     */

    private static CommitMessages fixCaseInCommitMessages(CommitMessages messages) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return messages;
    }

    private static CommitMessages removeBadStarts(CommitMessages messages, String[] badNameStarts) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        messages.apply((String name) -> {
            for (String template : badNameStarts) {
                while (name.startsWith(template)) {
                    name = name.substring(template.length());
                }
            }
            return name;
        });
        return messages;
    }

    private static CommitMessages strip(CommitMessages messages) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        messages.apply((String name) -> name.strip());
        return messages;
    }

    private static CommitMessages fixBigCommitMessages(CommitMessages messages) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        // TODO implement
        return messages;
    }
}
