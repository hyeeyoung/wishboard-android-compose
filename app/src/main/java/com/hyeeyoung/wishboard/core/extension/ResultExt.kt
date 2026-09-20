package com.hyeeyoung.wishboard.core.extension

import retrofit2.HttpException
import timber.log.Timber

inline fun <T> Result<T>.onFailure(
    action: (exception: Throwable, errorCode: Int?, errorBody: String?) -> Unit,
): Result<T> {
    this.onFailure { exception ->
        var code: Int? = null
        var errorBody: String? = null

        when (exception) {
            is HttpException -> {
                code = exception.code()
                errorBody = exception.response()?.errorBody()?.string()
            }
        }

        if (code != 401) {
            action(exception, code, errorBody)
        }

        Timber.e(exception.message)
    }

    return this
}
