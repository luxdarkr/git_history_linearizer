package lin_idea;

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
import java.util.Map;
import java.util.TreeMap;

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

    String[] emptyParams = new String[0];

    public LinearizerToolWindow(ToolWindow toolWindow) {
        // Attach action listeners
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        linearizeButton.addActionListener(e -> linearize());
        openRepoButton.addActionListener(e -> onOpenRepoButton());
        repoRefreshButton.addActionListener(e -> refreshRepoPath());
        refIDRefreshButton.addActionListener(e -> onRefIDRefreshButton());

        // Set ToolWindow content
        statusLabel.setText("");
        repoRefreshButton.setIcon(refreshIcon);
        refIDRefreshButton.setIcon(refreshIcon);

        // Set repo path and ref id on start
        refreshRepoPath();
        onRefIDRefreshButton();

        LinearizerToolWindowRef.setRef(this);
    }

    public JPanel getContent() {
        return linearizerToolWindowContent;
    }

    // Get current working directory
    VirtualFile[] getContentRoots() {
        final Project project = ProjectManager.getInstance().getOpenProjects()[0];
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        //VirtualFile dirLocal = contentRoots[0];
        return contentRoots;
    }

    // This function adds ".git" to the end of path if it is not present
    Path fixRepoPath(String path) throws NoSuchFileException {
        Path repoDir = Paths.get(path);

        // Check path before appending .git
        if (!Files.exists(repoDir)) {
            setRedLabel("No such file or directory");
            throw new NoSuchFileException(path);
        }
        if (!path.matches("^(([^\\\\/]*[\\\\/])*)(\\.git[\\\\/]?)$")) {
            // If there is no ".git"
            // Append ".git"
            repoDir = repoDir.resolve(".git");
        }

        // Check path after appending .git
        if (!Files.exists(repoDir)) {
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

    // Check all input data and call linearizer
    public void linearize() {
        // Check repo path
        String repoPath = repoTextField.getText().trim();
        Path repoDir;
        try { repoDir = fixRepoPath(repoPath); }
        catch (NoSuchFileException e) {
            System.out.println(e.getMessage());
            setRedLabel("Incorrect repository path");
            return;
        }
        repoTextField.setText(repoDir.toString());

        // Create JGit repo object
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
            settings.put("strip", emptyParams);
        }
        if (fixCaseCheckBox.isSelected()) {
            settings.put("fixCase", emptyParams);
        }
        if (fixBadStartsCheckBox.isSelected()) {
            String[] badStarts = badStartsTextField.getText().split(",");
            ArrayList<String> newBadStarts = new ArrayList<>();
            for (int i = 0; i < badStarts.length; i++) {
                if (!badStarts[i].isEmpty()) {
                    newBadStarts.add(badStarts[i]);
                }
            }
            settings.put("badStarts", newBadStarts.toArray(new String[0]));
        }
        if (fixBigMessagesCheckBox.isSelected()) {
            settings.put("fixBig", emptyParams);
        }

        // Check Ref ID
        String refID = refIDTextField.getText();
        if (!refID.matches("^[^\\\\/]+([\\\\/][^\\\\/]+){2}$")) {
            setRedLabel("Incorrect branch name");
            return;
        }

        // Check start commit ID
        String startCommit = startCommitTextField.getText();
        if (startCommit.isEmpty()) {
            setRedLabel("Incorrect start commit id");
            return;
        }

        //LinStateService c = ServiceManager.getService(LinStateService.class);
        //c.setStartCommit(startCommitTextField.getText());

        // Run linearizer
        try {
            Linearizer.processRepo(repo, refID, startCommit, settings);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            setRedLabel("Linearizer failed");
            return;
        }

        // Display success
        setGreenLabel("OK");
    }

    // Sets repo path text field to current opened project
    void refreshRepoPath() {
        VirtualFile[] contentRoots = getContentRoots();
        if (contentRoots.length != 0) {
            repoTextField.setText(contentRoots[0].getPath() + "/.git");
        }
    }

    // Set RefID default value
    void onRefIDRefreshButton() {
        refIDTextField.setText("refs/heads/master");
    }

    // Display red status message
    private void setRedLabel(String text) {
        statusLabel.setText(text);
        statusLabel.setForeground(Color.RED);
    }

    // Display green status message
    private void setGreenLabel(String text) {
        statusLabel.setText(text);
        statusLabel.setForeground(Color.GREEN);
    }

    private void createUIComponents() {
        // fallthrough
    }

    public void setStartCommitID(String s) {
        startCommitTextField.setText(s);
    }
}
