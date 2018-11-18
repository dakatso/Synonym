package su.katso.synonym.common.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap
import su.katso.synonym.common.entities.ApiMethod
import su.katso.synonym.common.entities.AuthInfo
import su.katso.synonym.common.entities.EncryptionInfo
import su.katso.synonym.common.entities.TaskInfo

interface ApiService {

    @GET("query.cgi")
    fun query(@QueryMap params: Map<String, String>): Single<Map<String, ApiMethod>>

    @GET("encryption.cgi")
    fun encryption(@QueryMap params: Map<String, String>): Single<EncryptionInfo>

    @GET("auth.cgi")
    fun auth(@QueryMap params: Map<String, String>): Single<AuthInfo>

    @GET("DownloadStation/task.cgi")
    fun task(@QueryMap params: Map<String, String>): Single<TaskInfo>

    object BaseParams {
        const val API: String = "api"
        const val METHOD: String = "method"
        const val VERSION: String = "version"
        const val SID: String = "_sid"
    }

    object QueryParams {
        const val QUERY: String = "query"
    }

    object AuthParams {
        const val CLIENT_TIME: String = "client_time"
        const val SESSION: String = "session"
    }

    object TaskParams {
        const val ADDITIONAL: String = "additional"
        const val LIMIT: String = "limit"
        const val OFFSET: String = "offset"
    }

    object Api {
        const val INFO = "SYNO.API.Info"
        const val ENCRYPTION = "SYNO.API.Encryption"
        const val AUTH = "SYNO.API.Auth"
        const val TASK = "SYNO.DownloadStation.Task"
    }
}

