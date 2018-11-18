package su.katso.synonym.common.entities

import com.google.gson.annotations.SerializedName

data class AuthInfo(
    @SerializedName("is_portal_port")
    val isPortalPort: Boolean,
    @SerializedName("sid")
    val sid: String
)
