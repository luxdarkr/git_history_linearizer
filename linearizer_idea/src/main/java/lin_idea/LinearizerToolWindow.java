// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package lin_idea;

import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.wm.ToolWindow;
import org.eclipse.jgit.lib.Repository;

import javax.swing.*;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import lin_core.Linearizer;

public class LinearizerToolWindow {

    private JButton linearizeButton;
    private JPanel linearizerToolWindowContent;
    private JButton hideToolWindowButton;
    private JCheckBox stripCommitMessagesCheckBox;
    private JCheckBox fixCaseCheckBox;
    private JCheckBox fixBadStartsCheckBox;
    private JCheckBox fixBigMessagesCheckBox;
    private JTextField startCommitTextField;
    private JTextField refIDTextField;
    private JTextField repoTextField;
    private JButton openRepoButton;
    private JTextField badStartsTextField;

    //private Linearizer linearizerInstance;

    public LinearizerToolWindow(ToolWindow toolWindow) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        linearizeButton.addActionListener(e -> onLinearizeButton());
        openRepoButton.addActionListener(e -> onOpenRepoButton());
    }

    // Select repo directory
    public void onOpenRepoButton() {
        JFileChooser j = new JFileChooser();
        j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try {
            j.showOpenDialog(repoTextField);
        }
        catch (Exception e) {}
        repoTextField.setText(j.getSelectedFile().toString());
    }

    // VCS log toolbar
    public void onLinearizeButton() {
        //Repository repo = new Repository();
        Map<String, String[]> settings = new TreeMap<>();
        if (stripCommitMessagesCheckBox.isSelected()) {
            settings.put("strip", new String[0]);
        }
        if (fixCaseCheckBox.isSelected()) {
            settings.put("fixCase", new String[0]);
        }
        if (fixBadStartsCheckBox.isSelected()) {
            String[] badStarts = badStartsTextField.getText().split(",");
            for (int i = 0; i < badStarts.length; i++) {
                badStarts[i] = badStarts[i].trim();
            }
            settings.put("badStarts", badStarts);
        }



        try {
            Linearizer.processRepo(repoTextField.getText(), refIDTextField.getText(), startCommitTextField.getText(), settings);
        }
        catch(Exception e) {
            System.out.println("Linearizer failed with exception:" + e.getMessage());
        }
    }

    public JPanel getContent() {
        return linearizerToolWindowContent;
    }

}
