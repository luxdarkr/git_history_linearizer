package lin_idea

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
        name = "LinStateService",
        storages = [Storage("linearizer.xml")]
)
class LinStateService : PersistentStateComponent<LinState> {

    private var pluginState: LinState = LinState()

    override fun getState(): LinState? {
        println("getState")
        return pluginState
    }

    override fun loadState(state: LinState) {
        println("loadState")
        pluginState = state
    }
    
    companion object {
        @JvmStatic
        fun getInstance(): PersistentStateComponent<LinState> {
            return ServiceManager.getService(LinStateService::class.java)
        }
    }
}

