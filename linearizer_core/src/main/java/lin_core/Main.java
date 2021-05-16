package lin_core;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

//import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

// Code taken from here
// https://github.com/centic9/jgit-cookbook/tree/master/src/main/java/org/dstadler/jgit

public class Main {
	public void openrepo(String repoPath) {
		File repoDir = new File(repoPath);

		// Creating repo object
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try (Repository repository = builder.setGitDir(repoDir)
				.readEnvironment() // Scan environment GIT_* variables
				.findGitDir()      // Scan up the file system tree
				.build()) {
			System.out.println("Having repository: " + repository.getDirectory());

			// The Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
			final String refName = "refs/heads/master";
			Ref head = repository.findRef(refName);
			if (head != null) {
				System.out.println("Found head: " + head);
			} else {
				System.out.println("Cannot find head " + refName);
				throw new IOException();
			}

			// A RevWalk allows to walk over commits based on some filtering that is defined
			RevCommit start = null;
			RevCommit end = null;
			try (RevWalk walk = new RevWalk(repository)) {
				RevCommit commit = walk.parseCommit(head.getObjectId());

				end = commit;

				// Print last commit message in master
				System.out.println("\nCommit-Message: " + commit.getFullMessage());

				while (commit != null) {
					start = commit;
					// if (commit.getParentCount() != 0) {
					if (commit.getParents() != null && commit.getParentCount() == 1) {
						commit = commit.getParent(0);
					} else {
						break;
					}
				}
				walk.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, String[]> settings = new TreeMap<>();
			settings.put("badStarts", new String[] {"*", "+"});
			Linearizer.processRepo(repository, start, settings);
			//repoLinearizer.removeStarsAndPlusesInCommitMessages(start, end);
		}
		catch (IOException e) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!");
			System.out.println(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*catch (GitAPIException e) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!");
			System.out.println(e);
		}*/
	}
}