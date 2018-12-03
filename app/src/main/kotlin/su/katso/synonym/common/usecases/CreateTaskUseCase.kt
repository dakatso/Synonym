package su.katso.synonym.common.usecases

import android.content.SharedPreferences
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.auth.AuthPresentationModel
import su.katso.synonym.common.arch.SingleUseCase
import su.katso.synonym.common.entities.TaskInfo
import su.katso.synonym.common.network.ApiService
import su.katso.synonym.common.network.ApiService.Api
import su.katso.synonym.common.network.ApiService.BaseParams
import su.katso.synonym.common.network.ApiService.TaskParams

class CreateTaskUseCase(
    private val preferences: SharedPreferences,
    private val api: ApiService,
    private val uri: String
) : SingleUseCase<TaskInfo>() {

    override val single: Single<TaskInfo> = getSid()
        .flatMap { create(it, uri).andThen(getTasks(it)) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun getSid(): Single<String> {
        return Single.fromCallable {
            preferences.getString(AuthPresentationModel.PREF_SID, "")
        }
    }

    private fun create(sid: String, uri: String) = api.taskCreate(
        mapOf(
            BaseParams.SID to sid,
            BaseParams.API to Api.TASK,
            BaseParams.METHOD to "create",
            BaseParams.VERSION to "1",
            TaskParams.URI to uri
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

