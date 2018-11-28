package su.katso.synonym.tasks

import android.content.SharedPreferences
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.auth.AuthPresentationModel
import su.katso.synonym.common.arch.SingleUseCase
import su.katso.synonym.common.network.ApiService
import su.katso.synonym.common.network.ApiService.Api
import su.katso.synonym.common.network.ApiService.BaseParams
import su.katso.synonym.common.network.ApiService.TaskParams

class TaskPauseUseCase(
    private val preferences: SharedPreferences,
    private val api: ApiService,
    private val id: String
) : SingleUseCase<List<JsonObject>>() {

    override val single: Single<List<JsonObject>> = getSid()
        .flatMap { pause(it, id) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun getSid(): Single<String> {
        return Single.fromCallable {
            preferences.getString(AuthPresentationModel.PREF_SID, "")
        }
    }

    private fun pause(sid: String, id: String) = api.taskPause(
        mapOf(
            BaseParams.SID to sid,
            BaseParams.API to Api.TASK,
            BaseParams.METHOD to "pause",
            BaseParams.VERSION to "1",
            TaskParams.ID to id
        )
    )
}

