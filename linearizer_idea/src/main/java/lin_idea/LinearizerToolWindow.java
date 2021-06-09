// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package lin_idea;

import com.intellij.openapi.wm.ToolWindow;
//import lin_core.Linearizer;
import org.eclipse.jgit.lib.Repository;

import javax.swing.*;
import java.util.Calendar;

public class LinearizerToolWindow {

    private JButton linearizeButton;
    private JPanel linearizerToolWindowContent;
    private JButton hideToolWindowButton;
    private JLabel bottomLabel;
    private JCheckBox stripCommitMessagesCheckBox;
    private JCheckBox fixCaseCheckBox;
    private JCheckBox removeExtraSymbolCheckBox;
    private JCheckBox fixBigMessagesCheckBox;
    private JTextField startCommitTextField;
    private JTextField endCommitTextField;
    private JTextField repoTextField;
    private JButton openRepoButton;

    //private Linearizer linearizerInstance;

    public LinearizerToolWindow(ToolWindow toolWindow) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        linearizeButton.addActionListener(e -> onLinearizeButton());
    }

    public void onLinearizeButton() {
        // Get current date and time
        Calendar instance = Calendar.getInstance();
        bottomLabel.setText(
            (instance.get(Calendar.DAY_OF_MONTH)) + "/" +
            (instance.get(Calendar.MONTH) + 1) + "/" +
            (instance.get(Calendar.YEAR)) + "  " +
            (instance.get(Calendar.HOUR)) + ":" +
            (instance.get(Calendar.MINUTE)) + ":" +
            (instance.get(Calendar.SECOND))
        );

        //Repository repo = new Repository();

        //linearizerInstance = new Linearizer(repo);

    }

    public JPanel getContent() {
        return linearizerToolWindowContent;
    }

}
