package su.katso.synonym.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.view.clicks
import su.katso.synonym.R
import su.katso.synonym.auth.AuthPresentationModel.FillInputsCommand
import su.katso.synonym.auth.AuthPresentationModel.OpenTasksCommand
import su.katso.synonym.common.arch.BaseController
import su.katso.synonym.common.arch.HideKeyboardCommand
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.utils.hideKeyboard
import su.katso.synonym.common.utils.klog
import su.katso.synonym.common.utils.setError
import su.katso.synonym.common.utils.text
import su.katso.synonym.common.utils.textChanges
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

    override fun buttonLoginClicks() = btnLogin.clicks()
    override fun editTextAddressTextChanges() = tilAddress.textChanges()
    override fun editTextAccountTextChanges() = tilAccount.textChanges()
    override fun editTextPasswordTextChanges() = tilPassword.textChanges()

    override fun render(viewState: AuthViewState) {
        klog(Log.DEBUG, viewState)

        tilAddress.setError(viewState.isAddressError)
        tilAccount.setError(viewState.isAccountError)
        tilPassword.setError(viewState.isPasswordError)

        tilAddress.text = viewState.addressText
        tilAccount.text = viewState.accountText
        tilPassword.text = viewState.passwordText
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
}



