package su.katso.synonym.common.usecases

import android.content.SharedPreferences
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.auth.AuthPresentationModel.LoginParams
import su.katso.synonym.common.arch.SingleUseCase

class GetLoginParamsUseCase(
    private val preferences: SharedPreferences
) : SingleUseCase<LoginParams>() {

    override val single: Single<LoginParams> = getParams()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun getParams(): Single<LoginParams> {
        return Single.fromCallable {
            LoginParams(
                preferences.getString(PREF_AUTH_INPUT_ADDRESS, "").orEmpty(),
                preferences.getString(PREF_AUTH_INPUT_ACCOUNT, "").orEmpty(),
                preferences.getString(PREF_AUTH_INPUT_PASSWORD, "").orEmpty()
            )
        }
    }

    companion object {
        const val PREF_AUTH_INPUT_ADDRESS: String = "pref_auth_input_address"
        const val PREF_AUTH_INPUT_ACCOUNT: String = "pref_auth_input_account"
        const val PREF_AUTH_INPUT_PASSWORD: String = "pref_auth_input_password"
    }
}
