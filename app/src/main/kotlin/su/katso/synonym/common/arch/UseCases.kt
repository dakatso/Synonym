package su.katso.synonym.common.arch

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.rxkotlin.addTo

abstract class ObservableUseCase<T> {
    private val disposables = CompositeDisposable()
    protected abstract val observable: Observable<T>
    fun dispose() = disposables.clear()
    fun subscribe(observer: DisposableObserver<T>) = observable
        .subscribeWith(observer).addTo(disposables)
}

class ObservableObserver<T> {
    private var onStart: () -> Unit = {}
    private var onNext: (T) -> Unit = {}
    private var onComplete: () -> Unit = {}
    private var onError: (Throwable) -> Unit = {}

    fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    fun onNext(onNext: (T) -> Unit) {
        this.onNext = onNext
    }

    fun onError(onError: (Throwable) -> Unit) {
        this.onError = onError
    }

    fun onComplete(onComplete: () -> Unit) {
        this.onComplete = onComplete
    }

    fun build() = object : DisposableObserver<T>() {
        override fun onStart() = onStart.invoke()
        override fun onNext(t: T) = onNext.invoke(t)
        override fun onError(e: Throwable) = onError.invoke(e)
        override fun onComplete() = onComplete.invoke()
    }
}

abstract class SingleUseCase<T> {
    private val disposables = CompositeDisposable()
    protected abstract val single: Single<T>
    fun dispose() = disposables.clear()
    fun subscribe(observer: DisposableSingleObserver<T>) = single
        .subscribeWith(observer).addTo(disposables)
}

class SingleObserver<T> {
    private var onStart: () -> Unit = {}
    private var onSuccess: (T) -> Unit = {}
    private var onError: (Throwable) -> Unit = {}

    fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    fun onSuccess(onSuccess: (T) -> Unit) {
        this.onSuccess = onSuccess
    }

    fun onError(onError: (Throwable) -> Unit) {
        this.onError = onError
    }

    fun build() = object : DisposableSingleObserver<T>() {
        override fun onStart() = onStart.invoke()
        override fun onSuccess(t: T) = onSuccess.invoke(t)
        override fun onError(e: Throwable) = onError.invoke(e)
    }
}

abstract class CompletableUseCase {
    private val disposables = CompositeDisposable()
    protected abstract val completable: Completable
    fun dispose() = disposables.clear()
    fun subscribe(observer: DisposableCompletableObserver) = completable
        .subscribeWith(observer).addTo(disposables)
}

class CompletableObserver {
    private var onStart: () -> Unit = {}
    private var onComplete: () -> Unit = {}
    private var onError: (Throwable) -> Unit = {}

    fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    fun onError(onError: (Throwable) -> Unit) {
        this.onError = onError
    }

    fun onComplete(onComplete: () -> Unit) {
        this.onComplete = onComplete
    }

    fun build() = object : DisposableCompletableObserver() {
        override fun onStart() = onStart.invoke()
        override fun onError(e: Throwable) = onError.invoke(e)
        override fun onComplete() = onComplete.invoke()
    }
}
