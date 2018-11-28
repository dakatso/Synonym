package su.katso.synonym.common.entities

import com.google.gson.annotations.SerializedName

data class TaskAction(
    @SerializedName("error")
    val error: Int,
    @SerializedName("id")
    val id: String
)