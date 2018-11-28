package su.katso.synonym.tasks.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import su.katso.synonym.R
import su.katso.synonym.common.adapter.WrapperViewHolder
import su.katso.synonym.common.entities.Task
import su.katso.synonym.common.entities.Task.Status

class TaskViewObject(task: Task) {
    val id: String = task.id
    var status: Status = task.status
    var title: String = task.title
}

class TaskViewHolder(parent: ViewGroup) : WrapperViewHolder(parent) {
    private val tvTitle: TextView = findViewById(R.id.tvTitle)
    private val ivStatus: ImageView = findViewById(R.id.ivStatus)

    override fun onCreate(parent: ViewGroup): View {
        return inflate(R.layout.tasks_task_item, parent)
    }

    override fun onBind(any: Any) {
        val item = any as TaskViewObject
        tvTitle.text = item.title

        ivStatus.setImageResource(
            when (item.status) {
                Status.PAUSED -> R.drawable.ic_tasks_status_pause
                Status.SEEDING -> R.drawable.ic_tasks_status_seeding
                Status.HASH_CHECKING -> R.drawable.ic_tasks_status_checking
                Status.WAITING -> R.drawable.ic_tasks_status_waiting
                Status.DOWNLOADING -> R.drawable.ic_tasks_status_downloading
                Status.ERROR -> R.drawable.ic_tasks_status_error
                else -> R.drawable.ic_tasks_status_waiting
            }
        )

        itemView.setOnClickListener(item)
    }
}


