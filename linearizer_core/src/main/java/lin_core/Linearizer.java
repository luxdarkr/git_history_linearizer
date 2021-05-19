package lin_core;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.*;

public class Linearizer {
    public static class RepoNode {
        public Set<RepoNode> parents = new HashSet<>();
        public Set<RepoNode> childs = new HashSet<>();
        RevCommit commit;
    }

    public static class RepoTree {
        public RepoNode head;
        public Map<RevCommit, RepoNode> nodes = new HashMap<>();
    }

    public static CommitPair processRepo(Repository repo, String refName, String startCommitId, Map<String, String[]> settings) throws Exception { // TODO better method name and args
        if (repo == null || refName == null || startCommitId == null || settings == null) {
            throw new NullPointerException();
        }
        CommitMessages messages = new CommitMessages();
        // TODO walk in repo and find all commits from start
        // it seems like JGit doesn't support direct child getting

        RevCommit start = null;
        RepoTree tree = new RepoTree();
        try (RevWalk walk = new RevWalk(repo)) {
            Ref head = repo.findRef(refName);
            if (head == null) {
                System.out.println("Cannot find head " + refName);
                throw new IOException();
            }
            RevCommit commit = walk.parseCommit(head.getObjectId());
            start = walk.parseCommit(ObjectId.fromString(startCommitId));
            if (start == null) {
                throw new NullPointerException();
            }
            tree.head = new RepoNode();
            tree.head.commit = commit;

            RepoNode prevNode = null;
            while (commit != start && commit != null) {
                String commitId = commit.getId().toObjectId().toString().substring(7, 7 + 40);
                messages.set(walk.parseCommit(ObjectId.fromString(commitId)), commit.getFullMessage());
                if (commit.getParents() != null) {
                    int parentCount = commit.getParentCount();
                    if (parentCount == 1) {
                        RepoNode repoNode = new RepoNode();

                        tree.nodes.put(commit, repoNode);
                        if (prevNode != null) {
                            repoNode.childs.add(prevNode);
                        }
                        commit = commit.getParent(0);
                        prevNode = repoNode;
                    } else {
                        commit = null;
                    }
                } else {
                    commit = null;
                }
            }

            walk.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // use messages.apply(private func String -> String) ?
        if (settings.containsKey("badStarts")) {
            removeBadStartsInCommitMessages(messages, settings.get("badStarts"));
        }
        if (settings.containsKey("strip")) {
            stripCommitMessages(messages);
        }
        if (settings.containsKey("fixCase")) {
            fixBigCommitMessages(messages);
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
        messages.apply((String name) -> {
            if (name.length() == 0) {
                return name;
            }
            Character firstChar = name.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + name.substring(1);
            }
            return name;
        });
        return messages;
    }

    private static CommitMessages removeBadStartsInCommitMessages(CommitMessages messages, String[] badNameStarts) throws NullPointerException {
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

    private static CommitMessages stripCommitMessages(CommitMessages messages) throws NullPointerException {
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
