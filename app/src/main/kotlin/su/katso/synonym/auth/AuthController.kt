package su.katso.synonym.auth

import android.os.Bundle
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.error.NoScopeFoundException
import org.koin.standalone.get
import su.katso.synonym.common.arch.BaseController
import su.katso.synonym.common.arch.Command
import su.katso.synonym.common.arch.HideKeyboardCommand
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.inject.SESSION_SCOPE
import su.katso.synonym.common.usecases.GetLoginParamsUseCase
import su.katso.synonym.common.usecases.LoginUseCase
import su.katso.synonym.common.utils.getError

class AuthController(
    view: AuthView, private val arguments: Bundle = Bundle.EMPTY
) : BaseController<AuthView, AuthModel>(view, AuthModel()) {
    private var session: Scope? = null
    private var loginUseCase: LoginUseCase? = null

    override fun onFirstBind(view: AuthView) {
        get<GetLoginParamsUseCase>().interact {
            onSuccess {
                modifyState {
                    addressText = it.address
                    accountText = it.account
                    passwordText = it.password
                }
            }
        }
    }

    override fun onBind(view: AuthView) {
        bindTo(view.editTextAddressTextChanges()) {
            modifyState(false) { addressText = it.toString() }
        }

        bindTo(view.editTextAccountTextChanges()) {
            modifyState(false) { accountText = it.toString() }
        }

        bindTo(view.editTextPasswordTextChanges()) {
            modifyState(false) { passwordText = it.toString() }
        }

        bindTo(view.buttonLoginClicks()) {

            loginUseCase?.dispose()

            if (viewState.isInputValid()) {
                reCreateScope()

                sendCommand(HideKeyboardCommand())

                val params = LoginParams(
                    viewState.addressText,
                    viewState.accountText,
                    viewState.passwordText
                )

                loginUseCase = get { parametersOf(params) }
                loginUseCase?.interact {
                    onComplete {
                        sendCommand(OpenTasksCommand(arguments))
                    }
                    onError {
                        val error = it.getError()
                        error?.let { sendCommand(ToastCommand(it.toString())) }
                            ?: run { sendCommand(ToastCommand(it.message.orEmpty())) }
                    }
                }
            }

            modifyState {
                isAddressError = viewState.addressText.isEmpty()
                isAccountError = viewState.accountText.isEmpty()
                isPasswordError = viewState.passwordText.isEmpty()
            }
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

    class OpenTasksCommand(val arguments: Bundle) : Command
    class FillInputsCommand(val loginParams: LoginParams) : Command

    class LoginParams(
        val address: String,
        val account: String,
        val password: String
    )
}