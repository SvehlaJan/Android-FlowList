package tech.svehla.gratitudejournal.data.repository

import retrofit2.HttpException
import tech.svehla.gratitudejournal.domain.model.ErrorReason
import tech.svehla.gratitudejournal.domain.repository.ErrorHandler
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject

class GeneralErrorHandlerImpl @Inject constructor() : ErrorHandler {
    override fun processError(throwable: Throwable): ErrorReason {
        return when (throwable) {
            is IOException -> ErrorReason.Network
            is HttpException -> {
                when (throwable.code()) {
                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorReason.NotFound

                    // access denied
                    HttpURLConnection.HTTP_FORBIDDEN -> ErrorReason.AccessDenied

                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> ErrorReason.ServiceUnavailable

                    // all the others will be treated as unknown error
                    else -> ErrorReason.Unknown
                }
            }
            else -> ErrorReason.Unknown
        }
    }
}