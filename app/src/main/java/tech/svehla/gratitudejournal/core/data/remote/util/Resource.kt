package tech.svehla.gratitudejournal.core.data.remote.util

import tech.svehla.gratitudejournal.core.domain.model.ErrorReason

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()

    data class Loading<T>(val data: T? = null): Resource<T>()

    data class Error<T>(val error: ErrorReason) : Resource<T>()
}