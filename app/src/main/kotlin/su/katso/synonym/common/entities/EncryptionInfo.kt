package su.katso.synonym.common.entities

import com.google.gson.annotations.SerializedName

data class EncryptionInfo(
    @SerializedName("cipherkey")
    val cipherKey: String,
    @SerializedName("ciphertoken")
    val cipherToken: String,
    @SerializedName("public_key")
    val publicKey: String,
    @SerializedName("server_time")
    val serverTime: Long
)
