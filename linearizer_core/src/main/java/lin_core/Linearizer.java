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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Linearizer {
    public static class Settings {
        public boolean strip = false;
        public boolean fixCase = false;
        public String[] badStarts = null;
        public boolean fixBig = false;
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
        if (settings.containsKey("fixBig")){
            result.fixBig = true;
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
        git.checkout()
                .setName(refName)
                .call();
        RevCommit commit = walk.parseCommit(head.getObjectId());
        List<RevCommit> orderedCommits = getOrder(walk, commit, startCommit, git);
        createAndSwitchBranch(git, startCommit);
        Collections.reverse(orderedCommits);
        RevCommit lastCommit = null;
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
                newMessage = newMessage.strip();
                lastCommit = git.commit()
                        .setAmend(true)
                        .setMessage(newMessage)
                        .call();

            }
        }

        walk.dispose();
        git.close();
        return new CommitPair(startCommit, lastCommit);
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
        if (settings.fixBig) {
            result = fixBigCommitMessage(result);
        }
        return result;
    }

    public static String getCommitSHA(RevCommit commit) throws Exception {
        return commit.getId().toObjectId().toString().substring(7, 7 + 40);
    }

    private static List<RevCommit> getOrder(RevWalk walk, RevCommit headCommit, RevCommit startCommit, Git git) throws Exception {
        Map<RevCommit, AtomicInteger> childrenCounts = new TreeMap<>();
        Iterable<RevCommit> commits = git.log().call();
        for (RevCommit commit : commits) {
            walk.parseCommit(commit);
            for (RevCommit parent : commit.getParents()) {
                if (!childrenCounts.containsKey(parent)) {
                    childrenCounts.put(parent, new AtomicInteger(0));
                }
                childrenCounts.get(parent).incrementAndGet();
            }
        }
        List<RevCommit> orderedCommits = new LinkedList<>();
        Stack<RevCommit> branchStack = new Stack<>();
        RevCommit commit = headCommit;
        while (commit != null && commit != startCommit) {
            walk.parseCommit(commit);
            int parentsCount = commit.getParentCount();
            int childrenCount = childrenCounts.getOrDefault(commit, new AtomicInteger(0)).intValue();
            if (parentsCount == 1 && childrenCount <= 1) { // simple
                orderedCommits.add(commit);
                commit = commit.getParent(0);
                continue;
            }
            if (parentsCount == 2 && childrenCount <= 1) { // merge commit without branching
                branchStack.add(commit);
                commit = commit.getParent(0);
                continue;
            }
            if (parentsCount <= 1 && childrenCount >= 2) { // branch start from simple commit
                //childrenCounts.get(commit).decrementAndGet();
                if (branchStack.empty()) {
                    orderedCommits.add(commit);
                    if (parentsCount == 1) {
                        commit = commit.getParent(0);
                    } else {
                        commit = null;
                    }
                } else {
                    commit = branchStack.pop().getParent(1);
                }
                continue;
            }
            if (parentsCount == 2 && childrenCount >= 2) { // branch start from merge commit
                //childrenCounts.get(commit).decrementAndGet();
                if (branchStack.empty()) {
                    branchStack.add(commit);
                    commit = commit.getParent(0);
                } else {
                    commit = branchStack.pop().getParent(1);
                }
                continue;
            }
            if (parentsCount == 0) {
                commit = null;
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
        StringBuilder o = new StringBuilder(original);
        int i = 0;
        int split_index = 15;

        if ((i = o.indexOf(" ", i + split_index)) != -1){
            o.replace(i, i + 1, "\n\n");
        }

        original = o.toString();
        return original;
    }
}
