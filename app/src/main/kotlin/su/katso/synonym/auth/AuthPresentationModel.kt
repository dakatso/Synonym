package su.katso.synonym.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import org.koin.core.scope.Scope
import org.koin.error.NoScopeFoundException
import org.koin.standalone.get
import su.katso.synonym.common.arch.BasePresentationModel
import su.katso.synonym.common.arch.HideKeyboardCommand
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.inject.PREF_BASE_URL
import su.katso.synonym.common.inject.PREF_SCHEME
import su.katso.synonym.common.inject.SESSION_SCOPE
import su.katso.synonym.common.utils.getError

class AuthPresentationModel : BasePresentationModel<AuthViewController, AuthViewState>(AuthViewState()) {
    private var session: Scope? = null
    private var loginUseCase: LoginUseCase? = null

    override fun onBind(viewController: AuthViewController) {
        bindTo(viewController.buttonLogin()) {
            loginUseCase?.dispose()

            if (it.isValid()) {
                saveBaseUrl(it)
                reCreateScope()

                sendCommand(HideKeyboardCommand())

                loginUseCase = LoginUseCase(get(), get(), it.account, it.password)
                loginUseCase?.interact(
                    onComplete = {
                        sendCommand(OpenTasksCommand())
                    },
                    onError = {
                        val error = it.getError()
                        error?.let { sendCommand(ToastCommand(it.toString())) }
                            ?: run { sendCommand(ToastCommand(it.message.orEmpty())) }
                    }
                )
            }

            modifyState {
                isAddressError = it.address.isEmpty()
                isAccountsError = it.account.isEmpty()
                isPasswordError = it.password.isEmpty()
            }
        }
    }

    private fun saveBaseUrl(params: LoginParams) {
        get<SharedPreferences>().edit {
            putString(PREF_BASE_URL, params.address)
            putString(PREF_SCHEME, "http")
        }
    }

    private fun reCreateScope() {
        try {
            getKoin().getScope(SESSION_SCOPE).close()
        } catch (e: NoScopeFoundException) {
        }

        session = getKoin().createScope(SESSION_SCOPE)
    }

    companion object {
        const val PREF_SID = "pref_sid"
    }

    class OpenTasksCommand : Command

    class LoginParams(
        val address: String,
        val account: String,
        val password: String
    ) {
        fun isValid() = address.isNotEmpty()
                && account.isNotEmpty()
                && password.isNotEmpty()
    }
}