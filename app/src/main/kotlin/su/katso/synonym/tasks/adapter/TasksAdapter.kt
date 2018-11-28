package su.katso.synonym.tasks.adapter

import su.katso.synonym.common.adapter.BaseAdapter

class TasksAdapter : BaseAdapter() {
    init {
        addTypes(
            TaskViewObject::class to ::TaskViewHolder
        )
    }
}
