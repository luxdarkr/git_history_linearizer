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
        createAndSwitchBranch(git, startCommit);
        RevCommit commit = walk.parseCommit(head.getObjectId());
        List<RevCommit> orderedCommits = getOrder(walk, commit, startCommit);
        Collections.reverse(orderedCommits);
        for (RevCommit curCommit : orderedCommits) {
            String newMessage = fixString(curCommit.getFullMessage(), settings);
            if (newMessage != null) {
                if (curCommit.getParents().length == 0) {
                    git.cherryPick()
                            .include(curCommit)
                            .call();
                } else {
                    git.cherryPick()
                            .include(curCommit)
                            .setMainlineParentNumber(1) // parent index starts from 1 here
                            .call();
                }
                git.commit()
                        .setAmend(true)
                        .setMessage(newMessage)
                        .call();
            }
        }

        walk.dispose();
        git.close();
        return new CommitPair(null, null);
    }

    private static String fixString(String original, Settings settings) {
        String result = original;
        if (settings.badStarts != null) {
            result = removeBadStarts(result, settings.badStarts);
        }
        if (settings.strip) {
            result = strip(result);
        }
        if (settings.fixCase) {
            result = fixCase(result);
        }
        return result;
    }

    public static String getCommitSHA(RevCommit commit) throws Exception {
        return commit.getId().toObjectId().toString().substring(7, 7 + 40);
    }

    private static List<RevCommit> getOrder(RevWalk walk, RevCommit commit, RevCommit startCommit) throws Exception {
        List<RevCommit> orderedCommits = new LinkedList<>();
        while (commit != null) {
            walk.parseCommit(commit.getId());
            boolean noParents = false;
            if (commit.getParents() != null) {
                int parentCount = commit.getParentCount();
                if (parentCount == 1) {
                    orderedCommits.add(commit);
                    commit = commit.getParent(0);
                } else if (parentCount == 2) {
                    //orderedCommits.add(commit); // <- merge commits
                    RevCommit branchCommit = null;
                    RevCommit leftCommit = commit.getParent(0);
                    RevCommit rightCommit = commit.getParent(1);
                    Set<RevCommit> leftSet = new TreeSet<>();
                    Set<RevCommit> rightSet = new TreeSet<>();
                    walk.parseCommit(leftCommit.getId());
                    walk.parseCommit(rightCommit.getId());
                    while (leftCommit.getParents().length > 0 && rightCommit.getParents().length > 0) {
                        leftSet.add(leftCommit);
                        rightSet.add(rightCommit);
                        if (leftSet.contains(rightCommit)) {
                            branchCommit = rightCommit;
                            break;
                        }
                        if (rightSet.contains(leftCommit)) {
                            branchCommit = leftCommit;
                            break;
                        }
                        leftCommit = leftCommit.getParent(0);
                        rightCommit = rightCommit.getParent(0);
                        walk.parseCommit(leftCommit.getId());
                        walk.parseCommit(rightCommit.getId());
                    }
                    orderedCommits.addAll(getOrder(walk, commit.getParent(0), branchCommit));
                    orderedCommits.addAll(getOrder(walk, commit.getParent(1), branchCommit));
                    commit = branchCommit;
                } else {
                    noParents = true;
                }
            } else {
                noParents = true;
            }
            if (commit == startCommit) {
                //orderedCommits.add(commit);
                noParents = true;
            }
            if (noParents) {
                break;
            }
        }
        return orderedCommits;
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
