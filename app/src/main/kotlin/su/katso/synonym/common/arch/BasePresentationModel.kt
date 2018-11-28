package su.katso.synonym.common.arch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.koin.standalone.KoinComponent
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.PresentationModel.ViewState

abstract class BasePresentationModel<C : ViewController<VS>, VS : ViewState>(defaultState: VS) : PresentationModel, KoinComponent {

    private val unBindDisposable = CompositeDisposable()
    private val onClearDisposable = CompositeDisposable()

    private val viewState: BehaviorSubject<VS> = BehaviorSubject.createDefault(defaultState)

    private val commands = PublishSubject.create<Command>()
    private val valve = PublishSubject.create<Boolean>()
    private val commandsValve = commands.valve(valve)

    private var isFirstBind = true

    fun bindToLifecycle(lifecycleController: C) {
        lifecycleController.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            private fun resume() = onBindController(lifecycleController)

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            private fun pause() = onUnbind()

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            private fun destroy() = onCleared()
        })
    }

    private fun onBindController(viewController: C) {
        viewState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewController.render(it) }
            .addTo(unBindDisposable)

        commandsValve
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { viewController.react(it) }
            .addTo(unBindDisposable)

        valve.onNext(true)

        if (isFirstBind) {
            onFirstBind(viewController)
            isFirstBind = false
        }
        onBind(viewController)
    }

    private fun onUnbind() {
        valve.onNext(false)
        unBindDisposable.clear()
    }

    private fun onCleared() {
        onClearDisposable.clear()
        valve.onComplete()
        viewState.onComplete()
        commands.onComplete()
        onDestroy()
    }

    protected abstract fun onBind(viewController: C)
    protected open fun onFirstBind(viewController: C) {}
    protected open fun onDestroy() {}

    protected fun <T> bindTo(observable: Observable<T>, onNext: (T) -> Unit) {
        observable.subscribe(onNext).addTo(unBindDisposable)
    }

    protected fun sendCommand(command: Command) {
        commands.onNext(command)
    }

    protected fun modifyState(apply: VS.() -> Unit) {
        viewState.value?.let {
            viewState.onNext(it.apply { apply() })
        } ?: throw NullPointerException()
    }

    protected fun <T> ObservableUseCase<T>.interact(
        onStart: (() -> Unit)? = null,
        onNext: (T) -> Unit,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        subscribe(onStart, onNext, onComplete, onError)
            .addTo(onClearDisposable)
    }

    protected fun CompletableUseCase.interact(
        onStart: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        subscribe(onStart, onComplete, onError)
            .addTo(onClearDisposable)
    }

    protected fun <T> SingleUseCase<T>.interact(
        onStart: (() -> Unit)? = null,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ): Disposable {
        return subscribe(onStart, onSuccess, onError)
            .addTo(onClearDisposable)
    }

    private fun <C : Command> Observable<C>.valve(valve: Observable<Boolean>, bufferSize: Int? = null): Observable<C> {

        val commandObservable = withLatestFrom(valve)
        { command, isOpen -> command to isOpen }.share()

        return Observable.merge(
            commandObservable.filter { it.second }.map { it.first },
            commandObservable.filter { !it.second }.map { it.first }
                .buffer<Boolean, Boolean>(valve.distinctUntilChanged().filter { !it },
                    Function<Boolean, Observable<Boolean>> { valve.distinctUntilChanged().filter { it } })
                .map { if (bufferSize != null) it.takeLast(bufferSize) else it }
                .flatMapIterable { it })
            .publish()
            .apply { connect().addTo(onClearDisposable) }
    }
}