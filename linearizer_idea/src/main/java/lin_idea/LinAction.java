package lin_idea;

import com.intellij.dvcs.repo.AbstractRepositoryManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.impl.HashImpl;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryImpl;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import com.intellij.dvcs.ui.VcsLogOneCommitPerRepoAction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class LinAction extends VcsLogOneCommitPerRepoAction<GitRepository> {
    @Override
    public void update(@NotNull AnActionEvent e) {
        // System.out.println("Update");
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
        Iterator it = commits.entrySet().iterator();
        VcsFullCommitDetails details = (VcsFullCommitDetails)((Map.Entry)it.next()).getValue();
        StartCommitTextFieldRef.setText(
            details.getId().asString()
        );
    }
}
