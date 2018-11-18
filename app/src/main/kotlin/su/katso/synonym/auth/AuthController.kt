package su.katso.synonym.auth

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import su.katso.synonym.R
import su.katso.synonym.auth.AuthPresentationModel.KeyboardCommand
import su.katso.synonym.auth.AuthPresentationModel.LoginParams
import su.katso.synonym.auth.AuthPresentationModel.OpenTasksCommand
import su.katso.synonym.auth.AuthPresentationModel.ToastCommand
import su.katso.synonym.common.arch.BaseController
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.utils.hideKeyboard
import su.katso.synonym.common.utils.klog
import su.katso.synonym.tasks.TasksController

class AuthController : BaseController(), AuthViewController {
    override val content = R.layout.auth_controller
    override val presentationModel = AuthPresentationModel()
        .also { it.bind(this) }

    private lateinit var btnLogin: Button
    private lateinit var etAddress: TextInputLayout
    private lateinit var etAccount: TextInputLayout
    private lateinit var etPassword: TextInputLayout

    override fun View.initView() {
        btnLogin = findViewById(R.id.btnLogin)
        etAddress = findViewById(R.id.etAddress)
        etAccount = findViewById(R.id.etAccount)
        etPassword = findViewById(R.id.etPassword)
    }

    override fun buttonLogin(): Observable<LoginParams> {
        fun TextInputLayout.getText() = editText?.text?.toString() ?: ""

        return RxView.clicks(btnLogin)
            .map { LoginParams(etAddress.getText(), etAccount.getText(), etPassword.getText()) }
    }

    override fun render(viewState: AuthViewState) {
        klog(viewState)
        with(btnLogin) {
            setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (viewState.isLoading) android.R.color.holo_blue_light
                    else android.R.color.darker_gray
                )
            )
        }

        etAddress.error = if (viewState.isAddressError) " " else ""
        etAccount.error = if (viewState.isAccountsError) " " else ""
        etPassword.error = if (viewState.isPasswordError) " " else ""
    }

    override fun react(command: Command) {
        when (command) {
            is ToastCommand -> {
                applicationContext?.let {
                    Toast.makeText(it, command.text, Toast.LENGTH_SHORT).show()
                }
            }
            is OpenTasksCommand -> {
                router.popCurrentController()
                router.pushController(RouterTransaction.with(TasksController()))
            }

            is KeyboardCommand -> hideKeyboard()
        }
    }
}



