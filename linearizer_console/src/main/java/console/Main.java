package console;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.out;

import lin_core.Linearizer;
import org.apache.commons.io.FileUtils;

//import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;


// Parts taken from this repo:
// https://github.com/centic9/jgit-cookbook/tree/master/src/main/java/org/dstadler/jgit

public class Main {

    @Option(
            name = "-h",
            aliases = "--help",
            usage = "Shows help screen.",
            forbids = {"-l"}
            )
    private boolean help;

    @Option(
            name = "-l",
            aliases = "--linearize",
            handler = StringArrayOptionHandler.class,
            required = true,
            usage = "Performs linearization between first and last commits, then puts it in a new fork."
            )
    private List<String> list;

    @Argument
    private List<String> args = new ArrayList<String>();

    public static void main(String[] args) throws Exception {

        Main main = new Main();
        main.parseArgs(args);
    }

    public void parseArgs(String[] args) throws Exception {
        final CmdLineParser parser = new CmdLineParser(this);

        if (args.length < 1) {
            parser.printUsage(out);
            System.exit(-1);
        }
        try {
            parser.parseArgument(args);
        } catch(CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("Example: linearizer -h");
            System.err.println("Example: linearizer -l <repoPath> <start> [-s]");
        }

        if(help){
            parser.printUsage(out);
        }


        if(list.isEmpty() == false){
            String[] emptyParams = new String[0];
            Map<String, String[]> settings = new TreeMap<>();
            settings.put("badStarts", new String[] {"*", "+"});
            settings.put("strip", emptyParams);
            settings.put("fixCase", emptyParams);
            Linearizer.processRepo(list.get(0), list.get(1), list.get(2), settings  );
        }

    }

}