package su.katso.synonym.common.arch

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import su.katso.synonym.common.arch.PresentationModel.ViewState

class Model<VS : ViewState>(default: VS) {
    var value: VS = default
    private val publishSubject = PublishSubject.create<VS>()

    fun sendState(modifier: VS.() -> Unit = {}) {
        value = value.apply { modifier() }
        publishSubject.onNext(value)
    }

    fun modifyState(modifier: VS.() -> Unit) {
        value = value.apply { modifier() }
    }

    fun subscribe(renderer: (VS) -> Unit): Disposable {
        val disposable = publishSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(renderer)
        sendState()
        return disposable
    }

    fun unSubscribe() {
        publishSubject.onComplete()
    }
}