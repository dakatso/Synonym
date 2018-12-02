package su.katso.synonym.auth

import io.reactivex.Observable
import su.katso.synonym.common.arch.ViewController

interface AuthViewController : ViewController<AuthViewState> {
    fun buttonLoginClicks(): Observable<Unit>
    fun editTextAddressTextChanges(): Observable<CharSequence>
    fun editTextAccountTextChanges(): Observable<CharSequence>
    fun editTextPasswordTextChanges(): Observable<CharSequence>
}

