package su.katso.synonym.common.arch

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class ModelWrapper<M : MvcModel>(default: M) {
    var value: M = default
    private val publishSubject = PublishSubject.create<M>()

    fun modifyState(modifier: M.() -> Unit, isNeedSend: Boolean) {
        value = value.apply { modifier() }
        if (isNeedSend) publishSubject.onNext(value)
    }

    fun subscribe(renderer: (M) -> Unit): Disposable {
        val disposable = publishSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(renderer)
        publishSubject.onNext(value)
        return disposable
    }

    fun unSubscribe() {
        publishSubject.onComplete()
    }
}