package lin_core;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.*;
import java.util.function.Function;

public class CommitMessages {
    private Map<RevCommit, String> data;

    CommitMessages() {
        data = new TreeMap<RevCommit, String>();
    }

    void apply(Function<String, String> func) throws NullPointerException {
        if (func == null) {
            throw new NullPointerException();
        }
        for (Map.Entry<RevCommit, String> entry : data.entrySet()) {
            RevCommit key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null) {
                throw new NullPointerException();
            }
            set(key, func.apply(value));
        }
    }

    public String get(RevCommit commit) {
        // TODO does it need exception?
        return data.get(commit);
    }

    public void set(RevCommit commit, String name) {
        data.put(commit, name);
    }
}
