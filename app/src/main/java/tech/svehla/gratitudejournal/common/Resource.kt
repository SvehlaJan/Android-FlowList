package tech.svehla.gratitudejournal.common

import tech.svehla.gratitudejournal.domain.model.ErrorReason

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()

    data class Loading<T>(val data: T? = null): Resource<T>()

    data class Error<T>(val reason: ErrorReason) : Resource<T>()
}