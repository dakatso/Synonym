package su.katso.synonym.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import org.koin.core.scope.Scope
import org.koin.standalone.get
import su.katso.synonym.common.arch.BasePresentationModel
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.inject.PREF_BASE_URL
import su.katso.synonym.common.inject.PREF_SCHEME
import su.katso.synonym.common.inject.SESSION_SCOPE
import su.katso.synonym.common.utils.getError
import su.katso.synonym.common.utils.klog

class AuthPresentationModel : BasePresentationModel<AuthViewController, AuthViewState>(AuthViewState()) {
    private var session: Scope? = null
    private var loginUseCase: LoginUseCase? = null

    override fun intents(viewController: AuthViewController) {
        intent(viewController.buttonLogin()) {
            loginUseCase?.dispose()

            if (it.isValid()) {
                saveBaseUrl(it)
                reCreateScope()

                sendCommand(KeyboardCommand())

                loginUseCase = LoginUseCase(get(), it.account, it.password)
                loginUseCase?.interact(
                    onSuccess = {
                        klog(Log.DEBUG, it)
                        sendCommand(OpenTasksCommand())
                    },
                    onError = {
                        val error = it.getError()
                        error?.let { sendCommand(ToastCommand(it.toString())) }
                            ?: run { sendCommand(ToastCommand(it.message ?: "")) }
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
        session?.close()
        session = getKoin().createScope(SESSION_SCOPE)
    }

    override fun onDestroy() {
        session?.close()
    }

    class ToastCommand(val text: String) : Command
    class KeyboardCommand : Command
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