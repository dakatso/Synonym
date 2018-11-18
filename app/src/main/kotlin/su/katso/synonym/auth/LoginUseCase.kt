package su.katso.synonym.auth

import android.util.Base64
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.common.arch.SingleUseCase
import su.katso.synonym.common.entities.AuthInfo
import su.katso.synonym.common.entities.EncryptionInfo
import su.katso.synonym.common.network.ApiException
import su.katso.synonym.common.network.ApiService
import su.katso.synonym.common.network.ApiService.Api
import su.katso.synonym.common.network.ApiService.AuthParams
import su.katso.synonym.common.network.ApiService.BaseParams
import su.katso.synonym.common.network.ApiService.QueryParams
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class LoginUseCase(
    private val api: ApiService,
    private val account: String,
    private val password: String
) : SingleUseCase<AuthInfo>() {

    override val single: Single<AuthInfo> = query()
        .flatMap {
            if (it.containsKey(Api.ENCRYPTION)) encryption()
            else Single.error(ApiException(104))
        }.flatMap { auth(it, account, password) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun query() = api.query(
        mapOf(
            BaseParams.API to Api.INFO,
            BaseParams.METHOD to "query",
            BaseParams.VERSION to "1",
            QueryParams.QUERY to "all"
        )
    )

    private fun encryption() = api.encryption(
        mapOf(
            BaseParams.API to Api.ENCRYPTION,
            BaseParams.METHOD to "getinfo",
            BaseParams.VERSION to "1"
        )
    )

    private fun auth(info: EncryptionInfo, account: String, password: String): Single<AuthInfo> {
        val cipherToken = encrypt(account, password, info)

        return api.auth(
            mapOf(
                BaseParams.API to Api.AUTH,
                BaseParams.METHOD to "login",
                BaseParams.VERSION to "6",
                info.cipherKey to cipherToken,
                AuthParams.CLIENT_TIME to (System.currentTimeMillis() / 1000).toString(),
                AuthParams.SESSION to "DownloadStation"
            )
        )
    }

    private fun encrypt(account: String, password: String, info: EncryptionInfo): String {
        fun String.encode() = URLEncoder.encode(this, "UTF-8")

        val timeBias = (info.serverTime - (System.currentTimeMillis() / 1000))

        val pubKeySpec = X509EncodedKeySpec(Base64.decode(info.publicKey, Base64.NO_WRAP))
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(pubKeySpec)

        val timeToken = System.currentTimeMillis() / 1000 + timeBias
        val plainText = "${info.cipherToken}=$timeToken&account=${account.encode()}&passwd=${password.encode()}"

        val cipherData = Cipher.getInstance("RSA/ECB/PKCS1Padding").let {
            it.init(Cipher.ENCRYPT_MODE, publicKey)
            it.doFinal(plainText.toByteArray())
        }
        return Base64.encodeToString(cipherData, Base64.NO_WRAP)
    }
}