package su.katso.synonym.auth

import io.reactivex.Observable
import su.katso.synonym.common.arch.MvcView

class AuthContract {
    interface View : MvcView<AuthModel> {
        fun buttonLoginClicks(): Observable<Unit>
        fun editTextAddressTextChanges(): Observable<CharSequence>
        fun editTextAccountTextChanges(): Observable<CharSequence>
        fun editTextPasswordTextChanges(): Observable<CharSequence>
    }
}