package lin_idea;

import com.intellij.dvcs.repo.AbstractRepositoryManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsFullCommitDetails;
import org.jetbrains.annotations.NotNull;
import com.intellij.dvcs.ui.VcsLogOneCommitPerRepoAction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LinAction extends VcsLogOneCommitPerRepoAction<LinRepository> {
    @Override
    public void update(@NotNull AnActionEvent e) {
        // System.out.println("Update");
    }

    @Override
    protected @NotNull AbstractRepositoryManager<LinRepository> getRepositoryManager(@NotNull Project project) {
        return LinRepositoryManager.getInstance(project);
    }

    @Override
    protected @Nullable LinRepository getRepositoryForRoot(@NotNull Project project, @NotNull VirtualFile root) {
        return getRepositoryManager(project).getRepositoryForRootQuick(root);
    }

    @Override
    protected void actionPerformed(@NotNull final Project project, @NotNull final Map<LinRepository, VcsFullCommitDetails> commits) {
        System.out.println("Action performed");

    //    GitVcsSettings settings = GitVcsSettings.getInstance(project);
    //    GitResetMode defaultMode = ObjectUtils.notNull(settings.getResetMode(), GitResetMode.getDefault());
    //    GitNewResetDialog dialog = new GitNewResetDialog(project, commits, defaultMode);
    //    if (dialog.showAndGet()) {
    //      final GitResetMode selectedMode = dialog.getResetMode();
    //      settings.setResetMode(selectedMode);
    //      new Task.Backgroundable(project, GitBundle.message("git.reset.process"), true) {
    //        @Override
    //        public void run(@NotNull ProgressIndicator indicator) {
    //          Map<GitRepository, Hash> hashes = commits.keySet().stream().collect(
    //                                            Collectors.toMap(Function.identity(), repo -> commits.get(repo).getId()));
    //          new GitResetOperation(project, hashes, selectedMode, indicator).execute();
    //        }
    //      }.queue();
    //    }

    }
}
