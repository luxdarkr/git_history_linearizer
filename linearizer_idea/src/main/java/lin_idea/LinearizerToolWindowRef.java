package lin_idea;

// Public reference to LinearizerToolWindow
// to be able to call methods from LinAction.

// Concurrency and Mutex are not needed here
// because (LinAction.actionPerformed) and (JTextField direct editing)
// both perform in GUI thread.

public final class LinearizerToolWindowRef {
    public static LinearizerToolWindow toolWindowRef;
    private static boolean valid = false;

    public static void setRef(LinearizerToolWindow t) {
        toolWindowRef = t;
        valid = true;
    }

    public static LinearizerToolWindow getRef() {
        return toolWindowRef;
    }

    public static boolean isValid() { return valid; }
}
