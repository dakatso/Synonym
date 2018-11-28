package su.katso.synonym.common.arch

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.rxkotlin.addTo

abstract class SingleUseCase<T> {
    private val disposables = CompositeDisposable()
    protected abstract val single: Single<T>
    fun dispose() = disposables.clear()
    fun subscribe(
        onStart: (() -> Unit)? = null,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ): Disposable {
        return single.subscribeWith(object : DisposableSingleObserver<T>() {
            override fun onStart() = onStart?.invoke() ?: Unit
            override fun onSuccess(t: T) = onSuccess.invoke(t)
            override fun onError(e: Throwable) {
                e.printStackTrace()
                onError?.invoke(e)
            }
        }).addTo(disposables)
    }
}

abstract class CompletableUseCase {
    private val disposables = CompositeDisposable()
    protected abstract val completable: Completable
    fun dispose() = disposables.clear()
    fun subscribe(
        onStart: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Disposable {
        return completable.subscribeWith(object : DisposableCompletableObserver() {
            override fun onStart() = onStart?.invoke() ?: Unit
            override fun onComplete() = onComplete?.invoke() ?: Unit
            override fun onError(e: Throwable) {
                e.printStackTrace()
                onError?.invoke(e)
            }
        }).addTo(disposables)
    }
}

abstract class ObservableUseCase<T> {
    private val disposables = CompositeDisposable()
    protected abstract val observable: Observable<T>
    fun dispose() = disposables.clear()
    fun subscribe(
        onStart: (() -> Unit)? = null,
        onNext: (T) -> Unit,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Disposable {
        return observable.subscribeWith(object : DisposableObserver<T>() {
            override fun onStart() = onStart?.invoke() ?: Unit
            override fun onNext(t: T) = onNext.invoke(t)
            override fun onComplete() = onComplete?.invoke() ?: Unit
            override fun onError(e: Throwable) {
                e.printStackTrace()
                onError?.invoke(e)
            }
        }).addTo(disposables)
    }
}
