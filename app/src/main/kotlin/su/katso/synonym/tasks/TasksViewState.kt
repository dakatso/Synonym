package su.katso.synonym.tasks

import su.katso.synonym.common.arch.PresentationModel.ViewState

data class TasksViewState(
    var isLoading: Boolean = false,
    var tasks: List<Any> = listOf()
) : ViewState