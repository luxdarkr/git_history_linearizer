package lin_core;

import java.util.Map;
import java.util.TreeMap;

public class App {
    /*
      path to repository (temp)
      JGit does not support paths with Cyrillic characters
     */
    static String relativeRepoPath = "/tests/git_test_simple/.git";

    public static void main(String[] args) throws Exception {
        String currentDir = System.getProperty("user.dir");
        String newRepoPath = currentDir + relativeRepoPath;
        String[] emptyParams = new String[0];
        Map<String, String[]> settings = new TreeMap<>();
        settings.put("badStarts", new String[] {"*", "+"});
        settings.put("strip", emptyParams);
        settings.put("fixCase", emptyParams);
        Linearizer.processRepo(newRepoPath, "refs/heads/master", "e40fc2fbea20214634e22445d2339e59b5067017", settings);
    }
}
