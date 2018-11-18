package su.katso.synonym.auth

import io.reactivex.Observable
import su.katso.synonym.common.arch.ViewController

interface AuthViewController : ViewController<AuthViewState> {
    fun buttonLogin(): Observable<AuthPresentationModel.LoginParams>
}

