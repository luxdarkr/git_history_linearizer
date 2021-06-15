package lin_idea;

import com.intellij.dvcs.repo.AbstractRepositoryManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class LinRepositoryManager extends AbstractRepositoryManager<LinRepository> {
    protected LinRepositoryManager(@NotNull Project project) {
        super(LinVcs.getInstance(project), ".git");
    }

    @Override
    public @NotNull List<LinRepository> getRepositories() {
        return getRepositories(LinRepository.class);
    }

    @Override
    public boolean isSyncEnabled() {
        return false;
    }

    @NotNull
    public static LinRepositoryManager getInstance(@NotNull Project project) {
        return project.getService(LinRepositoryManager.class);
    }
}
