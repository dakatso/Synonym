package su.katso.synonym.common.arch

import androidx.lifecycle.LifecycleOwner
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.PresentationModel.ViewState

interface ViewController<VS : ViewState> : LifecycleOwner {
    fun render(viewState: VS)
    fun react(command: Command)
}

interface PresentationModel {
    interface Command
    interface ViewState
}

