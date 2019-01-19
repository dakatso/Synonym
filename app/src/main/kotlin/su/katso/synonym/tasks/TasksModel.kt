package su.katso.synonym.tasks

import su.katso.synonym.common.arch.MvcModel

data class TasksModel(
    var isLoading: Boolean = false,
    var tasks: List<Any> = listOf()
) : MvcModel