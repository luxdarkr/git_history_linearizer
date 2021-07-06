package console;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.out;

import lin_core.CommitPair;
import lin_core.Linearizer;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class Main {

    final CmdLineParser parser = new CmdLineParser(this);
    public void printUsage(){
        System.out.println("Linearizer -l <repo_path> <branch> <start> [message_fix_options]");
        parser.printUsage(out);
    }

    @Option(
            name = "-h",
            aliases = "--help",
            forbids ={"-l"},
            usage = "Shows help screen."
    )
    private boolean help = false;

    @Option(
            name = "-l",
            aliases = "--linearize",
            handler = StringArrayOptionHandler.class,
            forbids = {"-h"},
            usage = "Performs linearization between first and last commits, then puts it in a new fork."
    )
    private List<String> list;

    @Option(
            name = "-s",
            depends={"-l"},
            aliases = "--strip",
            usage = "Strips commit messages after linearize"
    )
    private boolean strip = false;

    @Option(
            name = "-f",
            depends={"-l"},
            aliases = "--fixcase",
            usage = "Fixes cases in commit messages after linearize"
    )
    private boolean fixcase = false;

    @Option(
            name = "-r",
            depends={"-l"},
            aliases = "--badstarts",
            usage = "Removes stars and pluses in commit messages after linearize"
    )
    private boolean badstarts = false;

    @Option(
            name = "-b",
            depends={"-l"},
            aliases = "--fixbig",
            usage = "Fixes big commit messages after linearize"
    )
    private boolean fixbig = false;

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.parseArgs(args);
    }

    public void parseArgs(String[] args) throws Exception {
        boolean usage = false;
        try {
            parser.parseArgument(args);


            if (args.length < 1) {
                System.out.println("Incorrect input. No arguments received. See the example");
                usage = true;
                System.exit(-1);
            }

        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
        }

        if(help){
            printUsage();
        }

        if(list != null){
            String repo = list.get(0);
            String branch = list.get(1);
            String commit = list.get(2);
            try {
                if (list.size() == 3) {
                    Path repo_path = Paths.get(repo);
                    if (Files.notExists(repo_path)){
                        System.out.println("Incorrect repository path");
                        usage = true;
                    }

                    if (commit.length() != 40){
                        System.out.println("Incorrect commit id");
                        usage = true;
                    }

                } else {
                    System.out.println("Incorrect argument amount.\nThere must be three: repo_path, branch, start_commit");
                    usage = true;
                }

                if(usage == true){
                    printUsage();
                }


                String[] emptyParams = new String[0];
                Map<String, String[]> settings = new TreeMap<>();

                if (badstarts){
                    settings.put("badStarts", new String[]{"*", "+"});
                }

                if (strip) {
                    settings.put("strip", emptyParams);
                }

                if (fixcase){
                    settings.put("fixCase", emptyParams);
                }

                if(fixbig){
                    settings.put("fixBig", emptyParams);
                }

                CommitPair linRes = Linearizer.processRepo(repo, branch, commit, settings);
                System.out.println(linRes.second.toString().substring(7, 47));
            } catch(CmdLineException e){
                System.err.println(e.getMessage());
            }
        }
    }
}