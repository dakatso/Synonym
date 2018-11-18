package su.katso.synonym.common.network

import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val jsonRoot = JsonParser().parse(response.body()?.string()).asJsonObject
        val isSuccess = jsonRoot.getAsJsonPrimitive("success").asBoolean

        val jsonResponse = (if (isSuccess) jsonRoot.getAsJsonObject("data")
        else jsonRoot.getAsJsonObject("error")).toString()

        return response.newBuilder()
            .code(if (isSuccess) 200 else 422)
            .message(jsonResponse)
            .body(ResponseBody.create(response.body()?.contentType(), jsonResponse))
            .build()
    }
}