package lin_idea;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
//import git4idea.GitVcs;
//import git4idea.i18n.GitBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class LinVcs extends AbstractVcs {

    //public static final Supplier<@Nls String> DISPLAY_NAME = GitBundle.messagePointer("git4idea.vcs.name");
    public static final @NonNls String NAME = "Git";

    public LinVcs(@NotNull Project project, String name) {
        super(project, name);
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "Git"; //DISPLAY_NAME.get();
    }

    @NotNull
    public static LinVcs getInstance(@NotNull Project project) {
        LinVcs inst = (LinVcs) ProjectLevelVcsManager.getInstance(project).findVcsByName(NAME);
        ProgressManager.checkCanceled();
        return Objects.requireNonNull(inst);
    }
}
