package su.katso.synonym.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import su.katso.synonym.R
import su.katso.synonym.auth.AuthPresentationModel.FillInputsCommand
import su.katso.synonym.auth.AuthPresentationModel.LoginParams
import su.katso.synonym.auth.AuthPresentationModel.OpenTasksCommand
import su.katso.synonym.common.arch.BaseController
import su.katso.synonym.common.arch.HideKeyboardCommand
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.utils.hideKeyboard
import su.katso.synonym.common.utils.setError
import su.katso.synonym.tasks.TasksController

class AuthController(args: Bundle = Bundle.EMPTY) : BaseController(args), AuthViewController {
    override val content = R.layout.auth_controller
    override val presentationModel = AuthPresentationModel()
        .also { it.bindToLifecycle(this) }

    private lateinit var btnLogin: Button
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilAccount: TextInputLayout
    private lateinit var tilPassword: TextInputLayout

    override fun View.initView() {
        btnLogin = findViewById(R.id.btnLogin)
        tilAddress = findViewById(R.id.tilAddress)
        tilAccount = findViewById(R.id.tilAccount)
        tilPassword = findViewById(R.id.tilPassword)
    }

    override fun buttonLogin(): Observable<LoginParams> {
        fun TextInputLayout.getText() = editText?.text?.toString().orEmpty()

        return RxView.clicks(btnLogin)
            .map { LoginParams(tilAddress.getText(), tilAccount.getText(), tilPassword.getText()) }
    }

    override fun render(viewState: AuthViewState) {
        tilAddress.setError(viewState.isAddressError)
        tilAccount.setError(viewState.isAccountError)
        tilPassword.setError(viewState.isPasswordError)
    }

    override fun react(command: Command) {
        when (command) {
            is ToastCommand -> {
                applicationContext?.let {
                    Toast.makeText(it, command.text, Toast.LENGTH_SHORT).show()
                }
            }
            is OpenTasksCommand -> {
                router.setRoot(RouterTransaction.with(TasksController()))
            }

            is HideKeyboardCommand -> hideKeyboard()

            is FillInputsCommand -> {
                tilAddress.text = command.loginParams.address
                tilAccount.text = command.loginParams.account
                tilPassword.text = command.loginParams.password
            }
        }
    }

    private var TextInputLayout.text: String
        get() = editText?.text?.toString().orEmpty()
        set(value) = editText?.setText(value) ?: Unit
}



