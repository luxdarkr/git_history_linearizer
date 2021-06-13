package lin_core;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
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

    public static class Settings {
        public boolean strip = false;
        public boolean fixCase = false;
        public String[] badStarts = null;
    }

    // TODO refactor in project scape
    // TODO move names
    // TODO add class for message fixing
    public static Settings generateSettings(Map<String, String[]> settings) throws Exception {
        Settings result = new Settings();
        if (settings.containsKey("badStarts")) {
            result.badStarts = settings.get("badStarts");
            for (String entry : result.badStarts) {
                if (entry == null) {
                    throw new NullPointerException();
                }
                if (entry.length() == 0) {
                    throw new Exception("empty string passed as bad start template");
                }
            }
        }
        if (settings.containsKey("strip")) {
            result.strip = true;
        }
        if (settings.containsKey("fixCase")) {
            result.fixCase = true;
        }
        return result;
    }

    public static class RepoTree {
        public RepoNode head;
        public Map<RevCommit, RepoNode> nodes = new HashMap<>();

        public void sout() {
            for (Map.Entry<RevCommit, RepoNode> entry : nodes.entrySet()) {
                System.out.println("");
                System.out.print(entry.getKey());
                System.out.print(entry.getValue().commit.getFullMessage());
                System.out.println("childs");
                for (RepoNode elem : entry.getValue().childs) {
                    System.out.print(elem.commit.getFullMessage());
                }
                System.out.println("parents");
                for (RepoNode elem : entry.getValue().parents) {
                    System.out.print(elem.commit.getFullMessage());
                }
            }
        }
    }

    public static Repository openRepo(String path) throws Exception {
        File repoDir = new File(path);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(repoDir).readEnvironment().findGitDir().build();
    }

    public static CommitPair processRepo(String repoPath, String refName, String startCommitId, Map<String, String[]> settings) throws Exception {
        return processRepo(openRepo(repoPath), refName, startCommitId, settings);
    }

    private static void createAndSwitchBranch(Git git, RevCommit commit) throws GitAPIException {
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
                .setStartPoint(commit)
                .call();
        git.checkout()
                .setName(resultBranchName)
                .call();
    }

    public static CommitPair processRepo(Repository repo, String refName, String startCommitId, Map<String, String[]> settingsMap) throws Exception { // TODO better method name and args
        if (repo == null || refName == null || startCommitId == null || settingsMap == null) {
            throw new NullPointerException();
        }
        Settings settings = generateSettings(settingsMap);
        Ref head = repo.findRef(refName);
        if (head == null) {
            System.out.println("Cannot find head " + refName);
            throw new IOException();
        }
        RevWalk walk = new RevWalk(repo);
        RevCommit startCommit = walk.parseCommit(ObjectId.fromString(startCommitId));
        if (startCommit == null) {
            throw new NullPointerException();
        }

        Git git = new Git(repo);
        RepoTree tree = buildTree(walk, head, startCommit);
        createAndSwitchBranch(git, startCommit);
        tree.sout();
        linearize(tree, git, startCommit, settings);

        walk.dispose();
        git.close();
        return new CommitPair(null, null);
    }

    private static String fixString(String original, Settings settings) {
        String result = original;
        if (settings.badStarts != null) {
            result = removeBadStarts(result, settings.badStarts);
        }
        if (settings.fixCase) {
            result = fixCase(result);
        }
        if (settings.strip) {
            result = strip(result);
        }
        return result;
    }

    public static String getCommitSHA(RevCommit commit) throws Exception {
        return commit.getId().toObjectId().toString().substring(7, 7 + 40);
    }

    private static RepoTree buildTree(RevWalk walk, Ref head, RevCommit startCommit) throws IOException {
        RepoTree tree = new RepoTree();
        RevCommit commit = walk.parseCommit(head.getObjectId());
        tree.head = new RepoNode();
        tree.head.commit = commit;
        tree.nodes.put(commit, tree.head);

        RepoNode prevNode = null;
        Queue<RevCommit> walkQue = new LinkedList<>();
        while (commit != null) {
            walk.parseCommit(commit.getId());
            boolean noParents = false;
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
                } else if (parentCount == 2) {
                    walkQue.add(commit.getParent(1));
                    // TODO fix DRY
                    RepoNode repoNode = new RepoNode();
                    if (prevNode != null) {
                        repoNode.childs.add(prevNode);
                    }
                    repoNode.commit = commit;
                    tree.nodes.put(commit, repoNode);

                    commit = commit.getParent(0);
                    prevNode = repoNode;
                } else {
                    noParents = true;
                }
            } else {
                noParents = true;
            }
            if (noParents || commit == startCommit) {
                if (walkQue.size() > 0) {
                    commit = walkQue.remove();
                } else {
                    commit = null;
                }
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
        return tree;
    }

    private static CommitPair linearize(RepoTree tree, Git git, RevCommit startCommit, Settings settings) throws Exception {
        if (tree == null || git == null || startCommit == null || settings == null) {
            throw new NullPointerException();
        }
        // TODO check branch name
        // TODO implement

        RepoNode node = tree.nodes.get(startCommit);
        while (node != null) {
            RevCommit cpCommit = node.commit;
            System.out.print(cpCommit.getFullMessage());
            String newMessage = fixString(cpCommit.getFullMessage(), settings);
            if (newMessage != null) {
                if (cpCommit.getParents().length == 0) {
                    git.cherryPick()
                            .include(cpCommit)
                            .call();
                } else {
                    git.cherryPick()
                            .include(cpCommit)
                            .setMainlineParentNumber(1) // parent index starts from 1 here
                            .call();
                }
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
        return new CommitPair(startCommit, startCommit);
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

    private static String fixCase(String original) throws NullPointerException {
        if (original.length() == 0) {
            return original;
        }
        Character firstChar = original.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return Character.toUpperCase(firstChar) + original.substring(1);
        }
        return original;
    }

    private static String removeBadStarts(String original, String[] badNameStarts) throws NullPointerException {
        String result = original;
        for (String template : badNameStarts) {
            while (result.startsWith(template)) {
                result = result.substring(template.length());
            }
        }
        return result;
    }

    private static String strip(String original) throws NullPointerException {
        return original.strip();
    }

    private static String fixBigCommitMessage(String original) throws NullPointerException {
        // TODO implement
        return original;
    }
}
