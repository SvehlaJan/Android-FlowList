package tech.svehla.gratitudejournal.core.data.remote.util

import io.ktor.client.features.ClientRequestException
import kotlinx.serialization.SerializationException
import tech.svehla.gratitudejournal.core.domain.model.ErrorReason
import tech.svehla.gratitudejournal.core.domain.util.ErrorHandler
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection

class ErrorHandlerImpl: ErrorHandler {
    override fun processError(throwable: Throwable): ErrorReason {
        Timber.e(throwable, "Handling error")
        return when (throwable) {
            is IOException -> ErrorReason.Network
            is ClientRequestException -> {
                when (throwable.response.status.value) {
                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorReason.NotFound

                    // access denied
                    HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_FORBIDDEN -> ErrorReason.AccessDenied

                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> ErrorReason.ServiceUnavailable

                    // all the others will be treated as unknown error
                    else -> ErrorReason.Unknown
                }
            }
            is SerializationException -> ErrorReason.DataParsing
            else -> ErrorReason.Unknown
        }
    }
}