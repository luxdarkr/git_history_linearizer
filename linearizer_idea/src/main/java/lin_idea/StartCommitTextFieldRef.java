package lin_idea;

import javax.swing.*;

// Public reference to JTextField containing commit hash
// to be able to modify it from LinAction.

// Concurrency and Mutex are not needed here
// because (LinAction.actionPerformed) and (JTextField direct editing)
// both perform in GUI thread.

public final class StartCommitTextFieldRef {
    public static JTextField startCommitTextField;
    private static boolean valid = false;
    public static void setRef(JTextField t) {
        startCommitTextField = t;
        valid = true;
    }
    public static void setText(String id) {
        if (valid) {
            startCommitTextField.setText(id);
        }
    }
}
