package su.katso.synonym.tasks

import org.koin.standalone.get
import su.katso.synonym.common.arch.BasePresentationModel
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.entities.Task.Status
import su.katso.synonym.common.utils.getError
import su.katso.synonym.tasks.ChangeTaskStatusUseCase.Method
import su.katso.synonym.tasks.adapter.TaskViewObject

class TasksPresentationModel : BasePresentationModel<TasksViewController, TasksViewState>(TasksViewState()) {

    override fun onFirstBind(viewController: TasksViewController) {
        getTaskList()
    }

    override fun onBind(viewController: TasksViewController) {
        bindTo(viewController.itemRecycleView()) {
            when (val item = it.item) {
                is TaskViewObject -> changeTaskStatus(
                    item.id, if (item.status == Status.PAUSED) Method.RESUME else Method.PAUSE
                )
            }
        }
    }

    private fun changeTaskStatus(id: String, method: Method) {
        ChangeTaskStatusUseCase(get(), get(), id, method).interact(

            onStart = {
                modifyState { isLoading = true }
            },

            onSuccess = {
                modifyState {
                    isLoading = false
                    tasks = it.tasks.map(::TaskViewObject)
                }
            },
            onError = {
                val error = it.getError()
                error?.let { sendCommand(ToastCommand(it.toString())) }
                    ?: run { sendCommand(ToastCommand(it.message.orEmpty())) }

                modifyState {
                    isLoading = false
                }
            }
        )
    }

    private fun getTaskList() {
        TaskListUseCase(get(), get()).interact(
            onStart = {
                modifyState { isLoading = true }
            },

            onNext = {
                modifyState {
                    isLoading = false
                    tasks = it.tasks.map(::TaskViewObject)
                }
            },
            onError = {
                val error = it.getError()
                error?.let { sendCommand(ToastCommand(it.toString())) }
                    ?: run { sendCommand(ToastCommand(it.message.orEmpty())) }

                modifyState {
                    isLoading = false
                }
            }
        )
    }
}