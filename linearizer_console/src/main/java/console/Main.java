package console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.out;

import lin_core.Linearizer;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class Main {

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

    @Argument
    private List<String> args = new ArrayList<String>();

    public static void main(String[] args) throws Exception {

        Main main = new Main();
        main.parseArgs(args);
    }

    public void parseArgs(String[] args) throws Exception {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

            if (args.length < 1) {
                System.err.println("Incorrect input. No arguments received. See the example");
                System.err.println("linearizer [-h|-l] arguments...");
                System.exit(-1);
            }

        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
        }

        if(help){
            parser.printUsage(out);
        }


        if(list != null){
            try {
                if (list.size() < 3) {
                    System.err.println("Incorrect input. See the example");
                    System.err.println("Linearizer -l <repo_path> <branch> <start_commit> [-r] [-s] [-f]");
                    System.exit(-1);
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

                Linearizer.processRepo(list.get(0), list.get(1), list.get(2), settings);
            } catch(CmdLineException e){
                System.err.println(e.getMessage());
            }
        }

    }

}