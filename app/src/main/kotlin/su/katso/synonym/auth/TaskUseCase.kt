package su.katso.synonym.auth

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import su.katso.synonym.common.arch.SingleUseCase
import su.katso.synonym.common.entities.TaskInfo
import su.katso.synonym.common.network.ApiService
import su.katso.synonym.common.network.ApiService.Api
import su.katso.synonym.common.network.ApiService.BaseParams
import su.katso.synonym.common.network.ApiService.TaskParams

class TaskUseCase(
    private val api: ApiService,
    private val _sid: String
) : SingleUseCase<TaskInfo>() {

    override val single: Single<TaskInfo> = task()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun task() = api.task(
        mapOf(
            BaseParams.SID to _sid,
            BaseParams.API to Api.TASK,
            BaseParams.METHOD to "list",
            BaseParams.VERSION to "1",
            TaskParams.OFFSET to "0",
            TaskParams.LIMIT to "100"
        )
    )
}

