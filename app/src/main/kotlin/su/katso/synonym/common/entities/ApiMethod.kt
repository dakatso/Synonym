package su.katso.synonym.common.entities

import com.google.gson.annotations.SerializedName

data class ApiMethod(
    @SerializedName("maxVersion")
    val maxVersion: String,
    @SerializedName("minVersion")
    val minVersion: String,
    @SerializedName("path")
    val path: String
)
