package su.katso.synonym.tasks

import android.util.Log
import org.koin.standalone.get
import su.katso.synonym.common.arch.BasePresentationModel
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.entities.Task.Status
import su.katso.synonym.common.utils.getError
import su.katso.synonym.common.utils.klog
import su.katso.synonym.tasks.adapter.TaskViewObject

class TasksPresentationModel : BasePresentationModel<TasksViewController, TasksViewState>(TasksViewState()) {

    override fun onFirstBind(viewController: TasksViewController) {
        taskList()
    }

    override fun onBind(viewController: TasksViewController) {
        bindTo(viewController.itemRecycleView()) {
            when (val item = it.item) {
                is TaskViewObject -> {
                    if (item.status == Status.PAUSED) taskResume(item.id)
                    else taskPause(item.id)
                }
            }
        }
    }

    private fun taskPause(id: String) {
        TaskPauseUseCase(get(), get(), id).interact(
            onSuccess = {
                klog(Log.DEBUG, it)
            }
        )
    }

    private fun taskResume(id: String) {
        TaskResumeUseCase(get(), get(), id).interact(
            onSuccess = {
                klog(Log.DEBUG, it)
            }
        )
    }

    private fun taskList() {
        TaskListUseCase(get(), get()).interact(
            onStart = {
                modifyState { isLoading = true }
            },

            onNext = {
                modifyState {
                    isLoading = false
                    tasks = it.tasks.map { TaskViewObject(it) }
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