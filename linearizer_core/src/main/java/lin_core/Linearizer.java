package lin_core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Linearizer {
    public static class RepoNode {
        public List<RepoNode> parents = new LinkedList<>();
        public List<RepoNode> childs = new LinkedList<>();
        RevCommit commit;
    }

    public static class RepoTree {
        public RepoNode head;
        public Map<RevCommit, RepoNode> nodes = new HashMap<>();
    }

    public static Repository openRepo(String path) throws Exception {
        File repoDir = new File(path);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(repoDir).readEnvironment().findGitDir().build();
    }

    public static CommitPair processRepo(String repoPath, String refName, String startCommitId, Map<String, String[]> settings) throws Exception {
        return processRepo(openRepo(repoPath), refName, startCommitId, settings);
    }

    public static CommitPair processRepo(Repository repo, String refName, String startCommitId, Map<String, String[]> settings) throws Exception { // TODO better method name and args
        if (repo == null || refName == null || startCommitId == null || settings == null) {
            throw new NullPointerException();
        }
        Ref head = repo.findRef(refName);
        if (head == null) {
            System.out.println("Cannot find head " + refName);
            throw new IOException();
        }
        RevWalk walk = new RevWalk(repo);
        RevCommit commit = walk.parseCommit(head.getObjectId());
        RevCommit startCommit = walk.parseCommit(ObjectId.fromString(startCommitId));
        if (startCommit == null) {
            throw new NullPointerException();
        }
        CommitMessages messages = new CommitMessages();

        Git git = new Git(repo);
        RepoTree tree = new RepoTree();
        tree.head = new RepoNode();
        tree.head.commit = commit;
        tree.nodes.put(commit, tree.head);

        RepoNode prevNode = null;
        while (commit != startCommit && commit != null) {
            String commitId = commit.getId().toObjectId().toString().substring(7, 7 + 40);
            messages.set(walk.parseCommit(ObjectId.fromString(commitId)), commit.getFullMessage());
            if (commit.getParents() != null) {
                int parentCount = commit.getParentCount();
                if (parentCount == 1) {
                    RepoNode repoNode = new RepoNode();
                    if (prevNode != null) {
                        repoNode.childs.add(prevNode);
                    }
                    repoNode.commit = commit;
                    tree.nodes.put(commit, repoNode);

                    commit = commit.getParent(0);
                    prevNode = repoNode;
                } else {
                    commit = null;
                }
            } else {
                commit = null;
            }
        }
        if (commit == startCommit) {
            RepoNode repoNode = new RepoNode();
            if (prevNode != null) {
                repoNode.childs.add(prevNode);
            }
            repoNode.commit = commit;
            tree.nodes.put(commit, repoNode);
        }
        // use messages.apply(private func String -> String) ?
        if (settings.containsKey("badStarts")) {
            removeBadStartsInCommitMessages(messages, settings.get("badStarts"));
        }
        if (settings.containsKey("strip")) {
            stripCommitMessages(messages);
        }
        if (settings.containsKey("fixCase")) {
            fixCaseInCommitMessages(messages);
        }
        String resultBranchName = "linearizer_work";

        List<Ref> branchNameRefs = git.branchList().call();
        List<String> branchNames = new LinkedList<>();
        for (Ref ref : branchNameRefs) {
            branchNames.add(ref.getName());
        }
        while (branchNames.contains("refs/heads/" + resultBranchName)) {
            resultBranchName += '_';
        }
        git.branchCreate()
                .setName(resultBranchName)
                .setStartPoint(startCommit)
                .call();
        git.checkout()
                .setName(resultBranchName)
                .call();
        RepoNode node = tree.nodes.get(startCommit);
        while (node != null) {
            RevCommit cpCommit = node.commit;
            String newMessage = messages.get(cpCommit);
            if (newMessage != null) {
                git.cherryPick()
                        .include(cpCommit)
                        .call();
                git.commit()
                        .setAmend(true)
                        .setMessage(newMessage)
                        .call();
            }
            if (node.childs.isEmpty()) {
                break;
            }
            node = node.childs.get(0);
        }

        walk.dispose();
        git.close();
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

    private static void fixCaseInCommitMessages(CommitMessages messages) throws NullPointerException {
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
    }

    private static void removeBadStartsInCommitMessages(CommitMessages messages, String[] badNameStarts) throws NullPointerException {
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
    }

    private static void stripCommitMessages(CommitMessages messages) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        messages.apply((String name) -> name.strip());
    }

    private static void fixBigCommitMessages(CommitMessages messages) throws NullPointerException {
        if (messages == null) {
            throw new NullPointerException();
        }
        // TODO implement
    }
}
