package su.katso.synonym.common.usecases

import android.content.SharedPreferences
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.auth.AuthController
import su.katso.synonym.common.arch.SingleUseCase
import su.katso.synonym.common.entities.TaskInfo
import su.katso.synonym.common.network.ApiService
import su.katso.synonym.common.network.ApiService.Api
import su.katso.synonym.common.network.ApiService.BaseParams
import su.katso.synonym.common.network.ApiService.TaskParams

class ChangeTaskStatusUseCase(
    private val preferences: SharedPreferences,
    private val api: ApiService,
    private val id: String,
    private val method: Method
) : SingleUseCase<TaskInfo>() {

    override val single: Single<TaskInfo> = getSid()
        .flatMap { sid ->
            changeStatus(sid, id).flatMap { getTasks(sid) }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun getSid(): Single<String> {
        return Single.fromCallable {
            preferences.getString(AuthController.PREF_SID, "")
        }
    }

    private fun changeStatus(sid: String, id: String) = api.taskChangeStatus(
        mapOf(
            BaseParams.SID to sid,
            BaseParams.API to Api.TASK,
            BaseParams.METHOD to method.value,
            BaseParams.VERSION to "1",
            TaskParams.ID to id
        )
    )

    private fun getTasks(sid: String) = api.taskList(
        mapOf(
            BaseParams.SID to sid,
            BaseParams.API to Api.TASK,
            BaseParams.METHOD to "list",
            BaseParams.VERSION to "1",
            TaskParams.OFFSET to "0",
            TaskParams.LIMIT to "100"
        )
    )

    enum class Method(val value: String) {
        PAUSE("pause"),
        RESUME("resume")
    }
}

