package su.katso.synonym.common.arch

import androidx.lifecycle.LifecycleOwner

interface MvcView<M : MvcModel> : LifecycleOwner {
    fun render(model: M)
    fun react(command: Command)
}
interface MvcController
interface MvcModel

