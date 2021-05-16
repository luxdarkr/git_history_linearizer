package lin_core;

import org.eclipse.jgit.revwalk.RevCommit;

public class CommitPair {
    public RevCommit first;
    public RevCommit second;

    CommitPair(RevCommit f, RevCommit s) {
        first = f;
        second = s;
    }
}
