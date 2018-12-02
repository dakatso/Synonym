package su.katso.synonym.common.usecases

import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.auth.AuthPresentationModel
import su.katso.synonym.auth.AuthPresentationModel.LoginParams
import su.katso.synonym.common.usecases.GetLoginParamsUseCase.Companion.PREF_AUTH_INPUT_ACCOUNT
import su.katso.synonym.common.usecases.GetLoginParamsUseCase.Companion.PREF_AUTH_INPUT_ADDRESS
import su.katso.synonym.common.usecases.GetLoginParamsUseCase.Companion.PREF_AUTH_INPUT_PASSWORD
import su.katso.synonym.common.arch.CompletableUseCase
import su.katso.synonym.common.entities.AuthInfo
import su.katso.synonym.common.entities.EncryptionInfo
import su.katso.synonym.common.inject.PREF_BASE_URL
import su.katso.synonym.common.inject.PREF_SCHEME
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
    private val preferences: SharedPreferences,
    private val api: ApiService,
    private val params: LoginParams
) : CompletableUseCase() {

    override val completable: Completable = query()
        .flatMap {
            if (it.containsKey(Api.ENCRYPTION)) encryption()
            else Single.error(ApiException(104))
        }
        .flatMap { auth(it, params.account, params.password) }
        .flatMapCompletable { saveSid(it.sid) }
        .andThen(saveLoginParams(params))
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

    private fun saveSid(sid: String): Completable {
        return Completable.fromCallable {
            preferences.edit(true) {
                putString(AuthPresentationModel.PREF_SID, sid)
            }
        }
    }

    private fun saveLoginParams(params: LoginParams): Completable {
        return Completable.fromCallable {
            preferences.edit(true) {
                putString(PREF_BASE_URL, params.address)
                putString(PREF_SCHEME, "http")
                putString(PREF_AUTH_INPUT_ADDRESS, params.address)
                putString(PREF_AUTH_INPUT_ACCOUNT, params.account)
                putString(PREF_AUTH_INPUT_PASSWORD, params.password)
            }
        }
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