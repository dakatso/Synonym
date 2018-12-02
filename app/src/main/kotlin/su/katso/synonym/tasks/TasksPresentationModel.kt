package su.katso.synonym.tasks

import org.koin.core.parameter.parametersOf
import org.koin.standalone.get
import su.katso.synonym.common.arch.BasePresentationModel
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.entities.Task.Status
import su.katso.synonym.common.usecases.ChangeTaskStatusUseCase
import su.katso.synonym.common.usecases.ChangeTaskStatusUseCase.Method
import su.katso.synonym.common.usecases.GetTaskListUseCase
import su.katso.synonym.common.utils.getError
import su.katso.synonym.tasks.adapter.TaskViewObject

class TasksPresentationModel : BasePresentationModel<TasksViewController, TasksViewState>(TasksViewState()) {

    override fun onFirstBind(controller: TasksViewController) {
        getTaskList()
    }

    override fun onBind(controller: TasksViewController) {
        bindTo(controller.itemRecycleView()) {
            when (val item = it.item) {
                is TaskViewObject -> changeTaskStatus(
                    item.id, if (item.status == Status.PAUSED) Method.RESUME else Method.PAUSE
                )
            }
        }
    }

    private fun changeTaskStatus(id: String, method: Method) {
        get<ChangeTaskStatusUseCase> { parametersOf(id, method) }.interact(
            onStart = {
                sendState { isLoading = true }
            },

            onSuccess = {
                sendState {
                    isLoading = false
                    tasks = it.tasks.map(::TaskViewObject)
                }
            },
            onError = {
                val error = it.getError()
                error?.let { sendCommand(ToastCommand(it.toString())) }
                    ?: run { sendCommand(ToastCommand(it.message.orEmpty())) }

                sendState {
                    isLoading = false
                }
            }
        )
    }

    private fun getTaskList() {
        get<GetTaskListUseCase>().interact(
            onStart = {
                sendState { isLoading = true }
            },

            onNext = {
                sendState {
                    isLoading = false
                    tasks = it.tasks.map(::TaskViewObject)
                }
            },
            onError = {
                val error = it.getError()
                error?.let { sendCommand(ToastCommand(it.toString())) }
                    ?: run { sendCommand(ToastCommand(it.message.orEmpty())) }

                sendState {
                    isLoading = false
                }
            }
        )
    }
}