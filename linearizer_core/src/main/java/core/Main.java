package core;

import java.io.IOException;
import java.io.File;
import org.apache.commons.io.FileUtils;

//import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

// Parts taken from this repo:
// https://github.com/centic9/jgit-cookbook/tree/master/src/main/java/org/dstadler/jgit

public class Main {
    public void openrepo() {
        // Путь к репозиторию (пока хардкод)
        File repoDir = new File("C:/rapidjson/.git");
        // File repoDir = new File("C:/Users/DDRDmakar/Documents/EDU/ТРПО/Tamagotchi-Atmega8");

        // Creating repo object
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try (Repository repository = builder.setGitDir(repoDir)
                .readEnvironment() // Scan environment GIT_* variables
                .findGitDir()      // Scan up the file system tree
                .build()) {
            System.out.println("Having repository: " + repository.getDirectory());

            // The Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
            Ref head = repository.findRef("refs/heads/master");
            System.out.println("Found head: " + head);

            // A RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit commit = walk.parseCommit(head.getObjectId());

                // Print last commit message in master
                System.out.println("\nCommit-Message: " + commit.getFullMessage());

                walk.dispose();
            }
        }
        catch (IOException e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!");
            System.out.println(e);
        }
		/*catch (GitAPIException e) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!");
			System.out.println(e);
		}*/
    }
}