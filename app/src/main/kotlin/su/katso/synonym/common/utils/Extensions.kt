package su.katso.synonym.common.utils

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IntRange
import com.bluelinelabs.conductor.Controller
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import retrofit2.HttpException
import su.katso.synonym.BuildConfig

fun Controller.hideKeyboard() {
    activity?.currentFocus?.let {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

//TextInputLayout
var TextInputLayout.text: String
    get() = editText?.text?.toString().orEmpty()
    set(value) {
        isHintAnimationEnabled = false
        editText?.setText(value)
        isHintAnimationEnabled = true
    }

fun TextInputLayout.textChanges(): Observable<CharSequence> = editText?.textChanges()?.skipInitialValue()
    ?: Observable.empty<CharSequence>()

fun TextInputLayout.setError(isError: Boolean) {
    error = if (isError) " " else ""
}

fun Throwable.getError() = (this as? HttpException)
    ?.response()?.errorBody()?.string()?.let {
        try {
            JsonParser().parse(it).asJsonObject.getAsJsonPrimitive("code").asInt
        } catch (e: JsonSyntaxException) {
            null
        }
    }

@Suppress("NOTHING_TO_INLINE")
inline fun klog(message: Any?) = klog(Log.DEBUG, message)

fun klog(@IntRange(from = 2, to = 6) level: Int, message: Any?) {
    if (!BuildConfig.DEBUG) return
    Throwable().stackTrace[1].run {
        val tag = "commontag"
        val fullMessage = "($fileName:$lineNumber): $message"
        when (level) {
            Log.VERBOSE -> Log.v(tag, fullMessage)
            Log.DEBUG -> Log.d(tag, fullMessage)
            Log.INFO -> Log.i(tag, fullMessage)
            Log.WARN -> Log.w(tag, fullMessage)
            Log.ERROR -> Log.e(tag, fullMessage)
            else -> throw IllegalArgumentException()
        }
    }
}