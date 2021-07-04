package lin_core;

import java.util.Map;
import java.util.TreeMap;

public class App {
    /*
      path to repository (temp)
      JGit does not support paths with Cyrillic characters
     */
    static String relativeRepoPath = "/../self/.git";

    public static void main(String[] args) throws Exception {
        String currentDir = System.getProperty("user.dir");
        String newRepoPath = currentDir + relativeRepoPath;
        String[] emptyParams = new String[0];
        Map<String, String[]> settings = new TreeMap<>();
        settings.put("badStarts", new String[] {"*", "+"});
        //settings.put("strip", emptyParams);
        //settings.put("fixCase", emptyParams);
        Map<String, String> tests = new TreeMap<>();
        tests.put("/tests/lin_datasets/modbus-rs/.git", "8fe192385886cfdb3e187395bcd05f69df7d126d");
        //tests.put("/tests/lin_datasets/rapidjson/.git", "0e2c686753f01f629d537ce1291ac55408adce82");
        //tests.put("/tests/lin_datasets/syto/.git", "70a27dff5389e35b4eb8f133fbf62744f27a7fbc");
        //tests.put("/tests/lin_datasets/TechnoEvents/.git", "58674c1e220890b1069156b0774c9cecf294480d");
        //tests.put("/tests/lin_datasets/vkbottle/.git", "1f24e66ab51ceab553395799f578cfcd096ec2bf");
        for (Map.Entry<String, String> entry : tests.entrySet()) {
            try {
                //Linearizer.processRepo(currentDir + entry.getKey(), "refs/heads/master", entry.getValue(), settings);
            } catch (Exception e) {
                System.out.println("error: " + entry.getKey());
                System.out.println(e.getMessage());
            }
        }
        Linearizer.processRepo(newRepoPath, "refs/heads/master", "c4826c2ff26d2832fbffcab56b39804a9ef6a1a8", settings);
    }
}
