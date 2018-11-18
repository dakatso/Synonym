package su.katso.synonym.common.entities

import com.google.gson.annotations.SerializedName

data class TaskInfo(
    @SerializedName("offset")
    val offset: Long,
    @SerializedName("total")
    val total: Long,
    @SerializedName("tasks")
    val tasks: List<Task>
)
