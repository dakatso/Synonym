package su.katso.synonym.auth

import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.error.NoScopeFoundException
import org.koin.standalone.get
import su.katso.synonym.common.arch.BasePresentationModel
import su.katso.synonym.common.arch.HideKeyboardCommand
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.inject.SESSION_SCOPE
import su.katso.synonym.common.usecases.GetLoginParamsUseCase
import su.katso.synonym.common.usecases.LoginUseCase
import su.katso.synonym.common.utils.getError

class AuthPresentationModel : BasePresentationModel<AuthViewController, AuthViewState>(AuthViewState()) {
    private var session: Scope? = null
    private var loginUseCase: LoginUseCase? = null

    override fun onFirstBind(controller: AuthViewController) {
        get<GetLoginParamsUseCase>().interact(
            onSuccess = {
                sendState {
                    addressText = it.address
                    accountText = it.account
                    passwordText = it.password
                }
            }
        )
    }

    override fun onBind(controller: AuthViewController) {
        bindTo(controller.editTextAddressTextChanges()) {
            modifyState { addressText = it.toString() }
        }

        bindTo(controller.editTextAccountTextChanges()) {
            modifyState { accountText = it.toString() }
        }

        bindTo(controller.editTextPasswordTextChanges()) {
            modifyState { passwordText = it.toString() }
        }

        bindTo(controller.buttonLoginClicks()) {

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

            sendState {
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

    class OpenTasksCommand : Command
    class FillInputsCommand(val loginParams: LoginParams) : Command

    class LoginParams(
        val address: String,
        val account: String,
        val password: String
    )
}