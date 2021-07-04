package lin_idea;

import com.intellij.dvcs.repo.AbstractRepositoryManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsFullCommitDetails;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import com.intellij.dvcs.ui.VcsLogOneCommitPerRepoAction;
import org.jetbrains.annotations.Nullable;

// This action is invoked from git log context menu
// when pressing "Linearize from here" menu item.
// It fills in "repo path" and "start commit id" fields
// in plugin GUI,
// and after it starts linearizer

import java.util.Iterator;
import java.util.Map;

public class LinAction extends VcsLogOneCommitPerRepoAction<GitRepository> {
    @Override
    public void update(@NotNull AnActionEvent e) {
        // fallthrough
    }

    @Override
    protected @NotNull AbstractRepositoryManager<GitRepository> getRepositoryManager(@NotNull Project project) {
        return GitRepositoryManager.getInstance(project);
    }

    @Override
    protected @Nullable GitRepository getRepositoryForRoot(@NotNull Project project, @NotNull VirtualFile root) {
        return getRepositoryManager(project).getRepositoryForRootQuick(root);
    }

    @Override
    protected void actionPerformed(@NotNull final Project project, @NotNull final Map<GitRepository, VcsFullCommitDetails> commits) {
        // Get selected commit id
        Iterator it = commits.entrySet().iterator();
        VcsFullCommitDetails details = (VcsFullCommitDetails)((Map.Entry)it.next()).getValue();

        if (LinearizerToolWindowRef.isValid()) {
            // Fill in GUI text fields
            LinearizerToolWindowRef.getRef().setStartCommitID(
                    details.getId().asString()
            );
            LinearizerToolWindowRef.getRef().refreshRepoPath();

            // Start linearizer
            LinearizerToolWindowRef.getRef().linearize();

            // Update git log view
            // TODO no context is provided here
            //AnAction refreshAction = ActionManager.getInstance().getAction("Git.SelectInGitLog");
            //refreshAction.actionPerformed(EditorEx.getDataContext());
            //AnActionEvent e = new AnActionEvent();
            //VcsLogContentUtil.runInMainLog(project, logUi -> jumpToRevisionUnderProgress(project, logUi, hash));
        }
        else {
            System.out.println("Internal error: reference is not initialized");
        }
    }
}
