package su.katso.synonym.common.usecases

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.auth.AuthPresentationModel
import su.katso.synonym.common.arch.ObservableUseCase
import su.katso.synonym.common.entities.TaskInfo
import su.katso.synonym.common.network.ApiService
import su.katso.synonym.common.network.ApiService.Api
import su.katso.synonym.common.network.ApiService.BaseParams
import su.katso.synonym.common.network.ApiService.TaskParams
import java.util.concurrent.TimeUnit

class GetTaskListUseCase(
    private val preferences: SharedPreferences,
    private val api: ApiService
) : ObservableUseCase<TaskInfo>() {

    override val observable: Observable<TaskInfo> = getSid()
        .flatMapObservable { sid ->
            Observable.interval(0, 10, TimeUnit.SECONDS)
                .flatMapSingle { task(sid) }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun getSid(): Single<String> {
        return Single.fromCallable {
            preferences.getString(AuthPresentationModel.PREF_SID, "")
        }
    }

    private fun task(sid: String) = api.taskList(
        mapOf(
            BaseParams.SID to sid,
            BaseParams.API to Api.TASK,
            BaseParams.METHOD to "list",
            BaseParams.VERSION to "1",
            TaskParams.OFFSET to "0",
            TaskParams.LIMIT to "100"
        )
    )
}

