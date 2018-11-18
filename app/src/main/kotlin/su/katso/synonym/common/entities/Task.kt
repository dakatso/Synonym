package su.katso.synonym.common.entities

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class Task(
    @SerializedName("id")
    val id: String,
    @SerializedName("size")
    val size: Long,
    @JsonAdapter(StatusDeserializer::class)
    @SerializedName("status")
    val status: Status,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("username")
    val userName: String
) {
    enum class Status {
        WAITING, DOWNLOADING, PAUSED, FINISHING, FINISHED, HASH_CHECKING,
        SEEDING, FILEHOSTING_WAITING, EXTRACTING, ERROR, UNKNOWN;

        companion object {
            fun parseStatus(status: String) = when (status) {
                "waiting" -> WAITING
                "downloading" -> DOWNLOADING
                "paused" -> PAUSED
                "finishing" -> FINISHING
                "finished" -> FINISHED
                "hash_checking" -> HASH_CHECKING
                "seeding" -> SEEDING
                "filehosting_waiting" -> FILEHOSTING_WAITING
                "extracting" -> EXTRACTING
                "error" -> ERROR
                else -> UNKNOWN
            }
        }
    }

    class StatusDeserializer : JsonDeserializer<Status> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ) = Status.parseStatus(json.asString)
    }
}
