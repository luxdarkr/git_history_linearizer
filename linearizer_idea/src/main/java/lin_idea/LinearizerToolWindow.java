// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package lin_idea;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import org.eclipse.jgit.lib.Repository;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.PatternSyntaxException;

import lin_core.Linearizer;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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
    private JLabel statusLabel;
    private JButton repoRefreshButton;
    private JButton refIDRefreshButton;

    private Icon refreshIcon = IconLoader.getIcon("/icons/application/refresh.png");

    //private Linearizer linearizerInstance;

    public LinearizerToolWindow(ToolWindow toolWindow) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        linearizeButton.addActionListener(e -> onLinearizeButton());
        openRepoButton.addActionListener(e -> onOpenRepoButton());
        repoRefreshButton.addActionListener(e -> onRepoRefreshButton());
        refIDRefreshButton.addActionListener(e -> onRefIDRefreshButton());

        statusLabel.setText("");
        repoRefreshButton.setIcon(refreshIcon);
        refIDRefreshButton.setIcon(refreshIcon);

        onRepoRefreshButton();
        onRefIDRefreshButton();

        StartCommitTextFieldRef.setRef(startCommitTextField);
    }

    VirtualFile[] getContentRoots() {
        final Project project = ProjectManager.getInstance().getOpenProjects()[0];
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        //VirtualFile dirLocal = contentRoots[0];
        return contentRoots;
    }

    Path fixRepoPath(String path) throws NoSuchFileException {
        Path repoDir = Paths.get(path);
        if (!Files.exists(repoDir)) { // Check path before appending .git
            setRedLabel("No such file or directory");
            throw new NoSuchFileException(path);
        }
        if (!path.matches("^(([^\\\\/]*[\\\\/])*)(\\.git[\\\\/]?)$")) {
            repoDir = repoDir.resolve(".git");
        }
        if (!Files.exists(repoDir)) { // Check path after appending .git
            setRedLabel("No such file or directory");
            throw new NoSuchFileException(path);
        }
        return repoDir;
    }

    // Select repo directory
    public void onOpenRepoButton() {
        JFileChooser j = new JFileChooser();
        j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        j.showOpenDialog(repoTextField);
        File choice = j.getSelectedFile();
        if (choice == null) { /* fallthrough */ } // Cancel button pressed
        else { repoTextField.setText(choice.toString()); }
    }

    // VCS log toolbar
    public void onLinearizeButton() {
        String repoPath = repoTextField.getText().trim();
        Path repoDir;
        try { repoDir = fixRepoPath(repoPath); }
        catch (NoSuchFileException e) {
            System.out.println(e.getMessage());
            setRedLabel("Incorrect repository path");
            return;
        }
        repoTextField.setText(repoDir.toString());

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setGitDir(repoDir.toFile())
                .readEnvironment()
                .findGitDir();
        Repository repo;
        try { repo = builder.build(); }
        catch (IOException e) {
            System.out.println(e.getMessage());
            setRedLabel("Invalid repo path");
            return;
        }

        // Checkboxes processing
        Map<String, String[]> settings = new TreeMap<>();
        if (stripCommitMessagesCheckBox.isSelected()) {
            settings.put("strip", new String[0]);
        }
        if (fixCaseCheckBox.isSelected()) {
            settings.put("fixCase", new String[0]);
        }
        if (fixBadStartsCheckBox.isSelected()) {
            String[] badStarts = badStartsTextField.getText().split(",");
            ArrayList<String> newBadStarts = new ArrayList<String>();
            for (int i = 0; i < badStarts.length; i++) {
                if (!badStarts[i].isEmpty()) {
                    newBadStarts.add(badStarts[i]);
                }
            }
            settings.put("badStarts", newBadStarts.toArray(new String[0]));
        }

        String refID = refIDTextField.getText();
        if (!refID.matches("^[^\\\\/]+([\\\\/][^\\\\/]+){2}$")) {
            setRedLabel("Incorrect branch name");
            return;
        }
        String startCommit = startCommitTextField.getText();
        if (startCommit.isEmpty()) {
            setRedLabel("Incorrect start commit id");
            return;
        }

        //LinStateService c = ServiceManager.getService(LinStateService.class);
        //c.setStartCommit(startCommitTextField.getText());

        try {
            Linearizer.processRepo(repo, refID, startCommit, settings);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            setRedLabel("Linearizer failed");
            return;
        }
        setGreenLabel("OK");

    }

    void onRepoRefreshButton() {
        VirtualFile[] contentRoots = getContentRoots();
        if (contentRoots.length != 0) {
            repoTextField.setText(contentRoots[0].getPath() + "/.git");
        }
    }

    void onRefIDRefreshButton() {
        refIDTextField.setText("refs/heads/master");
    }

    public JPanel getContent() {
        return linearizerToolWindowContent;
    }

    private void setRedLabel(String text) {
        statusLabel.setText(text);
        statusLabel.setForeground(Color.RED);
    }

    private void setGreenLabel(String text) {
        statusLabel.setText(text);
        statusLabel.setForeground(Color.GREEN);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
